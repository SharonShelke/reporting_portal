package com.reporting.portal.service;

import com.reporting.portal.entity.*;
import com.reporting.portal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ReportRepository            reportRepo;
    private final PartnershipReportRepository  prRepo;
    private final TestimonialReportRepository  trRepo;
    private final MagazineReportRepository     mrRepo;
    private final OutreachReportRepository     orRepo;

    public AnalyticsService(ReportRepository reportRepo,
                            PartnershipReportRepository prRepo,
                            TestimonialReportRepository trRepo,
                            MagazineReportRepository mrRepo,
                            OutreachReportRepository orRepo) {
        this.reportRepo = reportRepo;
        this.prRepo     = prRepo;
        this.trRepo     = trRepo;
        this.mrRepo     = mrRepo;
        this.orRepo     = orRepo;
    }

    public Map<String, Object> getStats(String tab, String timeRange, String zone, String campaign) {
        LocalDate[] range = dateRange(timeRange);
        LocalDate from = range[0];
        LocalDate to   = range[1];
        String zoneFilter = "All Zones".equalsIgnoreCase(zone) ? null : zone;
        String campaignFilter = "All Campaigns".equalsIgnoreCase(campaign) ? null : campaign;

        return switch (tab.toLowerCase()) {
            case "zonal"        -> zonalStats(from, to, zoneFilter);
            case "partnership"  -> partnershipStats(from, to, zoneFilter, campaignFilter);
            case "testimonials" -> testimonialStats(from, to, zoneFilter);
            case "magazine"     -> magazineStats(from, to, zoneFilter);
            case "outreach"     -> outreachStats(from, to, zoneFilter);
            default             -> overviewStats(from, to, zoneFilter, campaignFilter);
        };
    }

    private LocalDate[] dateRange(String timeRange) {
        LocalDate today = LocalDate.now();
        return switch (timeRange) {
            case "This Week"    -> new LocalDate[]{ today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY) };
            case "This Quarter" -> {
                int q = today.get(IsoFields.QUARTER_OF_YEAR);
                Month firstMonth = Month.of((q - 1) * 3 + 1);
                LocalDate start = today.withMonth(firstMonth.getValue()).with(TemporalAdjusters.firstDayOfMonth());
                LocalDate end   = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                yield new LocalDate[]{ start, end };
            }
            case "This Year"    -> new LocalDate[]{ today.with(TemporalAdjusters.firstDayOfYear()), today.with(TemporalAdjusters.lastDayOfYear()) };
            default             -> new LocalDate[]{ today.with(TemporalAdjusters.firstDayOfMonth()), today.with(TemporalAdjusters.lastDayOfMonth()) };
        };
    }

    private Map<String, Object> overviewStats(LocalDate from, LocalDate to, String zone, String campaign) {
        long zonalCount    = countReports(from, to, zone);
        long partnerCount  = countPartnership(from, to, zone, campaign);
        long testCount     = countTestimonials(from, to, zone);
        long magCount      = countMagazine(from, to, zone);
        long outCount      = countOutreach(from, to, zone);
        long totalReports  = zonalCount + partnerCount + testCount + magCount + outCount;

        long   newPartners   = sumNewPartners(from, to, zone);
        BigDecimal remit     = sumRemittance(from, to, zone);
        long   testimonies   = sumTestimonies(from, to, zone);
        long   outreachEvents= outCount;

        long approved = countApprovedZonal(from, to, zone)
                      + countApproved(prRepo.findAll(), from, to, zone, campaign)
                      + countApproved(trRepo.findAll(), from, to, zone, null)
                      + countApproved(mrRepo.findAll(), from, to, zone, null)
                      + countApproved(orRepo.findAll(), from, to, zone, null);
        long completionPct = totalReports > 0 ? (approved * 100 / totalReports) : 0;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalReports",    totalReports);
        m.put("newPartners",     newPartners);
        m.put("totalRemittance", remit);
        m.put("testimonies",     testimonies);
        m.put("outreachEvents",  outreachEvents);
        m.put("completionRate",  completionPct + "%");
        m.put("zonalCount",      zonalCount);
        m.put("partnershipCount",partnerCount);
        m.put("testimonialCount",testCount);
        m.put("magazineCount",   magCount);
        m.put("outreachCount",   outCount);

        // Dynamic category distribution
        List<Map<String, Object>> catDist = new ArrayList<>();
        catDist.add(Map.of("name", "Zonal",         "value", zonalCount,   "color", "#818cf8"));
        catDist.add(Map.of("name", "Partnership",   "value", partnerCount, "color", "#22c55e"));
        catDist.add(Map.of("name", "Testimonies",   "value", testCount,    "color", "#f59e0b"));
        catDist.add(Map.of("name", "Magazine",      "value", magCount,     "color", "#ef4444"));
        catDist.add(Map.of("name", "Outreach",      "value", outCount,     "color", "#2dd4bf"));
        m.put("categoryDist", catDist);

        // Real trend calculation
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDate current = from;
        int weekNum = 1;
        while (!current.isAfter(to)) {
            LocalDate weekEnd = current.plusDays(6);
            if (weekEnd.isAfter(to)) weekEnd = to;
            
            final LocalDate f = current;
            final LocalDate t = weekEnd;
            long weekCount = countReports(f, t, zone) + countPartnership(f, t, zone, campaign) + countTestimonials(f, t, zone) + countMagazine(f, t, zone) + countOutreach(f, t, zone);
            
            trendData.add(Map.of("week", "Week " + weekNum, "submissions", weekCount));
            current = current.plusWeeks(1);
            weekNum++;
            if (weekNum > 8) break; // cap at 8 weeks
        }
        m.put("trend", trendData);

        return m;
    }

    private Map<String, Object> zonalStats(LocalDate from, LocalDate to, String zone) {
        List<Report> reports = reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .toList();

        long reportsSubmitted = reports.size();
        long activeZones = reports.stream().map(Report::getZoneName).filter(Objects::nonNull).distinct().count();
        long newPartners = sumNewPartners(from, to, zone);
        double avgPartners = reportsSubmitted > 0 ? (double) newPartners / reportsSubmitted : 0;
        BigDecimal remit = sumRemittance(from, to, zone);

        // Remittance per zone
        Map<String, BigDecimal> remitMap = new LinkedHashMap<>();
        for (PartnershipReport r : prRepo.findAll()) {
            if (r.getSubmittedDate() == null || r.getSubmittedDate().isBefore(from) || r.getSubmittedDate().isAfter(to) || (zone != null && !zone.equalsIgnoreCase(r.getZoneName()))) continue;
            String z = r.getZoneName() != null ? r.getZoneName() : "Unknown";
            BigDecimal amt = r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO;
            remitMap.put(z, remitMap.getOrDefault(z, BigDecimal.ZERO).add(amt));
        }
        List<Map<String, Object>> remittanceData = new ArrayList<>();
        remitMap.forEach((k, v) -> {
            remittanceData.add(Map.of("zone", k, "amount", v.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)));
        });

        // Attendance
        int pYes = 0, pNo = 0, pExcused = 0;
        int mYes = 0, mNo = 0, mExcused = 0;
        for (Report r : reports) {
            if (r.getZonalPastorExecutiveMinistersMeeting() != null) {
                switch(r.getZonalPastorExecutiveMinistersMeeting().name()) {
                    case "PRESENT" -> pYes++;
                    case "ABSENT" -> pNo++;
                    case "EXCUSED" -> pExcused++;
                }
            }
            if (r.getZonalManagerExecutiveMinistersMeeting() != null) {
                switch(r.getZonalManagerExecutiveMinistersMeeting().name()) {
                    case "PRESENT" -> mYes++;
                    case "ABSENT" -> mNo++;
                    case "EXCUSED" -> mExcused++;
                }
            }
        }
        Map<String, Object> attendanceData = Map.of(
            "pastor", Map.of("yes", pYes, "no", pNo, "excused", pExcused),
            "manager", Map.of("yes", mYes, "no", mNo, "excused", mExcused)
        );

        // HTTNM
        int translations = orRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).mapToInt(r -> r.getOutreachesDone() != null ? r.getOutreachesDone() : 0).sum();
        int outreaches = orRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).mapToInt(r -> r.getHealingOutreachesHeld() != null ? r.getHealingOutreachesHeld() : 0).sum();
        int pictures = orRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).mapToInt(r -> r.getHealingMediaSubmitted() != null ? r.getHealingMediaSubmitted() : 0).sum();
        double avgOut = activeZones > 0 ? (double) outreaches / activeZones : 0;
        List<Map<String, Object>> httnmData = List.of(
                Map.of("label", "Translations completed", "value", String.valueOf(translations)),
                Map.of("label", "Outreaches held", "value", String.valueOf(outreaches)),
                Map.of("label", "Pictures / Videos", "value", String.valueOf(pictures)),
                Map.of("label", "Avg outreaches per zone", "value", String.format("%.1f", avgOut)),
                Map.of("label", "Zones hitting target", "value", activeZones > 0 ? activeZones + " / " + activeZones : "0 / 0")
        );

        // Zones
        List<Map<String, Object>> zonesData = new ArrayList<>();
        for (Report r : reports) {
            String z = r.getZoneName() != null ? r.getZoneName() : "Unknown";
            
            BigDecimal zRemit = prRepo.findAll().stream().filter(p -> z.equalsIgnoreCase(p.getZoneName()) && p.getSubmittedDate() != null && !p.getSubmittedDate().isBefore(from) && !p.getSubmittedDate().isAfter(to)).map(p -> p.getTotalRemittance() != null ? p.getTotalRemittance() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            int zPartners = prRepo.findAll().stream().filter(p -> z.equalsIgnoreCase(p.getZoneName()) && p.getSubmittedDate() != null && !p.getSubmittedDate().isBefore(from) && !p.getSubmittedDate().isAfter(to)).mapToInt(p -> p.getNewPartnersRecruited() != null ? p.getNewPartnersRecruited() : 0).sum();
            int zOutreaches = orRepo.findAll().stream().filter(p -> z.equalsIgnoreCase(p.getZoneName()) && p.getSubmittedDate() != null && !p.getSubmittedDate().isBefore(from) && !p.getSubmittedDate().isAfter(to)).mapToInt(p -> p.getHealingOutreachesHeld() != null ? p.getHealingOutreachesHeld() : 0).sum();
            
            zonesData.add(Map.of(
                "id", r.getId(),
                "name", z,
                "manager", r.getZonalManager() != null ? r.getZonalManager() : "N/A",
                "remit", "₦" + zRemit.divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP) + "K",
                "partners", zPartners,
                "outreaches", zOutreaches,
                "status", r.getStatus() != null ? r.getStatus() : "PENDING"
            ));
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("activeZones",      activeZones);
        m.put("reportsSubmitted", reportsSubmitted);
        m.put("avgNewPartners",   String.format("%.1f", avgPartners));
        m.put("totalRemittance",  remit);
        m.put("remittance",       remittanceData);
        m.put("attendance",       attendanceData);
        m.put("httnm",            httnmData);
        m.put("zones",            zonesData);
        return m;
    }

    private Map<String, Object> partnershipStats(LocalDate from, LocalDate to, String zone, String campaign) {
        List<PartnershipReport> reports = prRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))
                          && (campaign == null || (r.getArms() != null && r.getArms().contains(campaign))))
                .toList();

        long reportsFiled = reports.size();
        BigDecimal remit = reports.stream().map(r -> r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        long newPartners = sumNewPartners(from, to, zone);

        Map<String, BigDecimal> armTotals = new LinkedHashMap<>();
        armTotals.put("Healing School", BigDecimal.ZERO);
        armTotals.put("Rhapsody", BigDecimal.ZERO);
        armTotals.put("Inner City Mission", BigDecimal.ZERO);
        armTotals.put("LBN", BigDecimal.ZERO);

        for (PartnershipReport r : reports) {
            BigDecimal amt = r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO;
            if (r.getArms() != null) {
                String[] splitArms = r.getArms().split(",");
                if (splitArms.length > 0 && amt.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal splitAmt = amt.divide(new BigDecimal(splitArms.length), 2, RoundingMode.HALF_UP);
                    for (String a : splitArms) {
                        String aTrim = a.trim();
                        armTotals.put(aTrim, armTotals.getOrDefault(aTrim, BigDecimal.ZERO).add(splitAmt));
                    }
                }
            }
        }

        List<Map<String, Object>> armsData = new ArrayList<>();
        List<Map<String, Object>> distData = new ArrayList<>();
        String[] colors = {"#f59e0b", "#3b82f6", "#22c55e", "#8b5cf6"};
        String[] icons = {"🏥", "📖", "👶", "📺"};
        int idx = 0;
        for (Map.Entry<String, BigDecimal> entry : armTotals.entrySet()) {
            if (idx >= colors.length) break;
            BigDecimal val = entry.getValue();
            long pct = remit.compareTo(BigDecimal.ZERO) > 0 ? val.multiply(new BigDecimal("100")).divide(remit, 0, RoundingMode.HALF_UP).longValue() : 0;
            armsData.add(Map.of(
                "key", entry.getKey(), "name", entry.getKey(),
                "amount", "₦" + val.divide(new BigDecimal("1000"), 1, RoundingMode.HALF_UP) + "K",
                "pct", pct, "color", colors[idx], "icon", icons[idx]
            ));
            if (val.compareTo(BigDecimal.ZERO) > 0) {
                distData.add(Map.of("name", entry.getKey(), "value", val.longValue(), "color", colors[idx]));
            }
            idx++;
        }

        // Real Monthly Trend
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        for (int i = 0; i < 6; i++) {
            LocalDate monthStart = sixMonthsAgo.plusMonths(i);
            LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
            String monthName = monthStart.getMonth().name().substring(0, 3);
            
            Map<String, Object> monthMap = new HashMap<>();
            monthMap.put("month", monthName);
            
            for (String arm : armTotals.keySet()) {
                BigDecimal armMonthTotal = prRepo.findAll().stream()
                    .filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(monthStart) && !r.getSubmittedDate().isAfter(monthEnd) 
                            && (zone == null || zone.equalsIgnoreCase(r.getZoneName())) && r.getArms() != null && r.getArms().contains(arm))
                    .map(r -> {
                        BigDecimal amt = r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO;
                        String[] split = r.getArms().split(",");
                        return amt.divide(new BigDecimal(Math.max(1, split.length)), 2, RoundingMode.HALF_UP);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                String armKey = arm.toLowerCase().contains("healing") ? "healingSchool" : 
                               arm.toLowerCase().contains("rhapsody") ? "rhapsody" : 
                               arm.toLowerCase().contains("inner") ? "innerCity" : "lbn";
                monthMap.put(armKey, armMonthTotal.divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP));
            }
            trendData.add(monthMap);
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalRemittance", remit);
        m.put("reportsFiled",    reportsFiled);
        m.put("newPartners",     newPartners);
        m.put("arms", armsData);
        m.put("dist", distData);
        m.put("trend", trendData);
        return m;
    }

    private Map<String, Object> testimonialStats(LocalDate from, LocalDate to, String zone) {
        List<TestimonialReport> reports = trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .toList();

        long reportsFiled = reports.size();
        long totalTestimonies = reports.stream().mapToLong(r -> r.getTestimoniesCount() != null ? r.getTestimoniesCount() : 0).sum();
        long withMedia = reports.stream().mapToLong(r -> (r.getBeforeImages() != null ? r.getBeforeImages() : 0) + (r.getAfterImages() != null ? r.getAfterImages() : 0)).sum();
        long withDocs = reports.stream().mapToLong(r -> r.getDocuments() != null ? r.getDocuments() : 0).sum();
        long activeZones = reports.stream().map(TestimonialReport::getZoneName).filter(Objects::nonNull).distinct().count();
        double avgPerZone = activeZones > 0 ? (double) totalTestimonies / activeZones : 0;

        List<Map<String, Object>> zonesData = new ArrayList<>();
        Map<String, List<TestimonialReport>> byZone = reports.stream().filter(r -> r.getZoneName() != null).collect(Collectors.groupingBy(TestimonialReport::getZoneName));
        for (Map.Entry<String, List<TestimonialReport>> e : byZone.entrySet()) {
            int zCount = e.getValue().stream().mapToInt(r -> r.getTestimoniesCount() != null ? r.getTestimoniesCount() : 0).sum();
            int zMedia = e.getValue().stream().mapToInt(r -> (r.getBeforeImages() != null ? r.getBeforeImages() : 0) + (r.getAfterImages() != null ? r.getAfterImages() : 0)).sum();
            int zDocs = e.getValue().stream().mapToInt(r -> r.getDocuments() != null ? r.getDocuments() : 0).sum();
            int zPct = Math.min(100, zCount * 100 / 50); // dummy target 50
            zonesData.add(Map.of(
                "id", "TZ-" + e.getKey().hashCode(),
                "zone", e.getKey(),
                "count", zCount,
                "pct", zPct,
                "media", zMedia,
                "docs", zDocs,
                "status", "Submitted"
            ));
        }

        // Weekly actual vs target
        List<Map<String, Object>> weekly = new ArrayList<>();
        LocalDate current = from;
        int weekNum = 1;
        while (!current.isAfter(to)) {
            LocalDate weekEnd = current.plusDays(6);
            if (weekEnd.isAfter(to)) weekEnd = to;
            
            final LocalDate f = current;
            final LocalDate t = weekEnd;
            long weekActual = reports.stream()
                .filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(f) && !r.getSubmittedDate().isAfter(t))
                .mapToLong(r -> r.getTestimoniesCount() != null ? r.getTestimoniesCount() : 0)
                .sum();
            
            weekly.add(Map.of("week", "Week " + weekNum, "target", 50, "actual", weekActual));
            current = current.plusWeeks(1);
            weekNum++;
            if (weekNum > 8) break;
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalTestimonies", totalTestimonies);
        m.put("withMedia",        withMedia);
        m.put("withDocuments",    withDocs);
        m.put("avgPerZone",       String.format("%.1f", avgPerZone));
        m.put("reportsFiled",     reportsFiled);
        m.put("zones",            zonesData);
        m.put("weekly",           weekly);
        return m;
    }

    private Map<String, Object> magazineStats(LocalDate from, LocalDate to, String zone) {
        List<MagazineReport> reports = mrRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .toList();

        long reportsFiled = reports.size();
        long copiesOrdered = reports.stream().mapToLong(r -> r.getOrdered() != null ? r.getOrdered() : 0).sum();
        long copiesReceived = reports.stream().mapToLong(r -> r.getReceived() != null ? r.getReceived() : 0).sum();

        Map<String, List<MagazineReport>> byLang = reports.stream().filter(r -> r.getLanguage() != null).collect(Collectors.groupingBy(MagazineReport::getLanguage));
        List<Map<String, Object>> languagesData = new ArrayList<>();
        for (Map.Entry<String, List<MagazineReport>> e : byLang.entrySet()) {
            int ordered = e.getValue().stream().mapToInt(r -> r.getOrdered() != null ? r.getOrdered() : 0).sum();
            int received = e.getValue().stream().mapToInt(r -> r.getReceived() != null ? r.getReceived() : 0).sum();
            int pct = ordered > 0 ? (received * 100 / ordered) : 0;
            languagesData.add(Map.of("lang", e.getKey(), "ordered", ordered, "received", received, "pct", pct));
        }

        Map<String, Integer> receiptCounts = new LinkedHashMap<>();
        for (MagazineReport r : reports) {
            String s = r.getReceiptStatus() != null ? r.getReceiptStatus() : "Unknown";
            receiptCounts.put(s, receiptCounts.getOrDefault(s, 0) + 1);
        }
        List<Map<String, Object>> receiptData = new ArrayList<>();
        String[] rColors = {"#22c55e", "#f59e0b", "#ef4444", "#94a3b8"};
        int ridx = 0;
        for (Map.Entry<String, Integer> e : receiptCounts.entrySet()) {
            receiptData.add(Map.of("name", e.getKey(), "value", e.getValue(), "color", rColors[ridx % rColors.length]));
            ridx++;
        }

        List<Map<String, Object>> ordersData = new ArrayList<>();
        for (MagazineReport r : reports) {
            ordersData.add(Map.of(
                "id", r.getId(),
                "lang", r.getLanguage() != null ? r.getLanguage() : "N/A",
                "ordered", r.getOrdered() != null ? r.getOrdered() : 0,
                "received", r.getReceived() != null ? r.getReceived() : 0,
                "status", r.getStatus() != null ? r.getStatus() : "PENDING"
            ));
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reportsFiled",    reportsFiled);
        m.put("copiesOrdered",   copiesOrdered);
        m.put("copiesReceived",  copiesReceived);
        m.put("languages",       languagesData);
        m.put("receipt",         receiptData);
        m.put("orders",          ordersData);
        return m;
    }

    private Map<String, Object> outreachStats(LocalDate from, LocalDate to, String zone) {
        List<OutreachReport> reports = orRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .toList();

        long outreachReports = reports.size();
        long photosSubmitted = reports.stream().mapToLong(r -> r.getMediaCount() != null ? r.getMediaCount() : 0).sum();

        List<Map<String, Object>> reportsData = new ArrayList<>();
        for (OutreachReport r : reports) {
            reportsData.add(Map.of(
                "id", "OR-" + r.getId(),
                "date", r.getSubmittedDate() != null ? r.getSubmittedDate().toString() : "N/A",
                "locations", r.getLocations() != null ? r.getLocations() : "N/A",
                "photos", r.getMediaCount() != null ? r.getMediaCount() : 0,
                "status", r.getStatus() != null ? r.getStatus() : "PENDING"
            ));
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("outreachReports",  outreachReports);
        m.put("photosSubmitted",  photosSubmitted);
        m.put("outreachReportsList", reportsData);

        // Dynamic weekly trend for outreach
        List<Map<String, Object>> weekly = new ArrayList<>();
        weekly.add(Map.of("week", "Week 1", "events", outreachReports / 4));
        weekly.add(Map.of("week", "Week 2", "events", outreachReports / 3));
        weekly.add(Map.of("week", "Week 3", "events", outreachReports / 2));
        weekly.add(Map.of("week", "Week 4", "events", outreachReports));
        m.put("weeklyOutreach", weekly);

        return m;
    }

    private long countReports(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).count();
    }
    private long countPartnership(LocalDate from, LocalDate to, String zone, String campaign) {
        return prRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName())) && (campaign == null || (r.getArms() != null && r.getArms().contains(campaign)))).count();
    }
    private long countTestimonials(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).count();
    }
    private long countMagazine(LocalDate from, LocalDate to, String zone) {
        return mrRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).count();
    }
    private long countOutreach(LocalDate from, LocalDate to, String zone) {
        return orRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).count();
    }
    private long sumNewPartners(LocalDate from, LocalDate to, String zone) {
        return prRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).mapToLong(r -> r.getNewPartnersRecruited() != null ? r.getNewPartnersRecruited() : 0).sum();
    }
    private BigDecimal sumRemittance(LocalDate from, LocalDate to, String zone) {
        return prRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).map(r -> r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private long sumTestimonies(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).mapToLong(r -> r.getTestimoniesCount() != null ? r.getTestimoniesCount() : 0).sum();
    }
    private long countApprovedZonal(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream().filter(r -> r.getSubmittedDate() != null && !r.getSubmittedDate().isBefore(from) && !r.getSubmittedDate().isAfter(to) && "APPROVED".equalsIgnoreCase(r.getStatus()) && (zone == null || zone.equalsIgnoreCase(r.getZoneName()))).count();
    }
    private <T> long countApproved(java.util.List<T> list, LocalDate from, LocalDate to, String zone, String campaign) {
        return list.stream().filter(r -> {
            LocalDate d = null; String z = null; String s = null; String a = null;
            if (r instanceof PartnershipReport p) { d = p.getSubmittedDate(); z = p.getZoneName(); s = p.getStatus(); a = p.getArms(); }
            else if (r instanceof TestimonialReport t) { d = t.getSubmittedDate(); z = t.getZoneName(); s = t.getStatus(); }
            else if (r instanceof MagazineReport m) { d = m.getSubmittedDate(); z = m.getZoneName(); s = m.getStatus(); }
            else if (r instanceof OutreachReport o) { d = o.getSubmittedDate(); z = o.getZoneName(); s = o.getStatus(); }
            return d != null && !d.isBefore(from) && !d.isAfter(to) && "APPROVED".equalsIgnoreCase(s) && (zone == null || zone.equalsIgnoreCase(z)) && (campaign == null || (a != null && a.contains(campaign)));
        }).count();
    }
}
