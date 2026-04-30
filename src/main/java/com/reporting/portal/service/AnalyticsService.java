package com.reporting.portal.service;

import com.reporting.portal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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

    // ─────────────────────────────────────────────────────
    // Public entry-point
    // ─────────────────────────────────────────────────────
    public Map<String, Object> getStats(String tab, String timeRange, String zone) {
        LocalDate[] range = dateRange(timeRange);
        LocalDate from = range[0];
        LocalDate to   = range[1];
        String zoneFilter = "All Zones".equalsIgnoreCase(zone) ? null : zone;

        return switch (tab.toLowerCase()) {
            case "zonal"        -> zonalStats(from, to, zoneFilter);
            case "partnership"  -> partnershipStats(from, to, zoneFilter);
            case "testimonials" -> testimonialStats(from, to, zoneFilter);
            case "magazine"     -> magazineStats(from, to, zoneFilter);
            case "outreach"     -> outreachStats(from, to, zoneFilter);
            default             -> overviewStats(from, to, zoneFilter);
        };
    }

    // ─────────────────────────────────────────────────────
    // Date-range helper
    // ─────────────────────────────────────────────────────
    private LocalDate[] dateRange(String timeRange) {
        LocalDate today = LocalDate.now();
        return switch (timeRange) {
            case "This Week"    -> new LocalDate[]{ today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY) };
            case "This Quarter" -> {
                int q = today.get(IsoFields.QUARTER_OF_YEAR);
                LocalDate qStart = today.with(IsoFields.QUARTER_OF_YEAR, q).with(TemporalAdjusters.firstDayOfMonth()).withDayOfMonth(1);
                // first month of quarter
                Month firstMonth = Month.of((q - 1) * 3 + 1);
                LocalDate start = today.withMonth(firstMonth.getValue()).with(TemporalAdjusters.firstDayOfMonth());
                LocalDate end   = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                yield new LocalDate[]{ start, end };
            }
            case "This Year"    -> new LocalDate[]{ today.with(TemporalAdjusters.firstDayOfYear()), today.with(TemporalAdjusters.lastDayOfYear()) };
            default             -> new LocalDate[]{ today.with(TemporalAdjusters.firstDayOfMonth()), today.with(TemporalAdjusters.lastDayOfMonth()) };
        };
    }

    // ─────────────────────────────────────────────────────
    // Overview
    // ─────────────────────────────────────────────────────
    private Map<String, Object> overviewStats(LocalDate from, LocalDate to, String zone) {
        long zonalCount    = countReports(from, to, zone);
        long partnerCount  = countPartnership(from, to, zone);
        long testCount     = countTestimonials(from, to, zone);
        long magCount      = countMagazine(from, to, zone);
        long outCount      = countOutreach(from, to, zone);
        long totalReports  = zonalCount + partnerCount + testCount + magCount + outCount;

        long   newPartners   = sumNewPartners(from, to, zone);
        BigDecimal remit     = sumRemittance(from, to, zone);
        long   testimonies   = sumTestimonies(from, to, zone);
        long   outreachEvents= outCount;

        // Completion rate = approved across all report types / total
        long approved = countApprovedZonal(from, to, zone)
                      + countApproved(prRepo.findAll(), from, to, zone)
                      + countApproved(trRepo.findAll(), from, to, zone)
                      + countApproved(mrRepo.findAll(), from, to, zone)
                      + countApproved(orRepo.findAll(), from, to, zone);
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
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Zonal
    // ─────────────────────────────────────────────────────
    private Map<String, Object> zonalStats(LocalDate from, LocalDate to, String zone) {
        long reportsSubmitted = countReports(from, to, zone);
        long activeZones      = countDistinctZones(from, to, zone);
        double avgPartners    = reportsSubmitted > 0 ? (double) sumNewPartners(from, to, zone) / reportsSubmitted : 0;
        BigDecimal remit      = sumRemittance(from, to, zone);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("activeZones",      activeZones);
        m.put("reportsSubmitted", reportsSubmitted);
        m.put("avgNewPartners",   String.format("%.1f", avgPartners));
        m.put("totalRemittance",  remit);
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Partnership
    // ─────────────────────────────────────────────────────
    private Map<String, Object> partnershipStats(LocalDate from, LocalDate to, String zone) {
        long reportsFiled = countPartnership(from, to, zone);
        BigDecimal remit  = sumPartnershipRemittance(from, to, zone);
        long newPartners  = sumNewPartners(from, to, zone);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalRemittance", remit);
        m.put("reportsFiled",    reportsFiled);
        m.put("newPartners",     newPartners);
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Testimonials
    // ─────────────────────────────────────────────────────
    private Map<String, Object> testimonialStats(LocalDate from, LocalDate to, String zone) {
        long totalTestimonies = sumTestimonialCount(from, to, zone);
        long reportsFiled     = countTestimonials(from, to, zone);

        // Media = sum of beforeImages + afterImages
        long withMedia = sumTestimonialMedia(from, to, zone);
        long withDocs  = sumTestimonialDocs(from, to, zone);
        double avgPerZone = countDistinctZonesTestimonials(from, to, zone) > 0
                ? (double) totalTestimonies / countDistinctZonesTestimonials(from, to, zone) : 0;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalTestimonies", totalTestimonies);
        m.put("withMedia",        withMedia);
        m.put("withDocuments",    withDocs);
        m.put("avgPerZone",       String.format("%.1f", avgPerZone));
        m.put("reportsFiled",     reportsFiled);
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Magazine
    // ─────────────────────────────────────────────────────
    private Map<String, Object> magazineStats(LocalDate from, LocalDate to, String zone) {
        long reportsFiled   = countMagazine(from, to, zone);
        long copiesOrdered  = sumMagazineOrdered(from, to, zone);
        long copiesReceived = sumMagazineReceived(from, to, zone);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reportsFiled",    reportsFiled);
        m.put("copiesOrdered",   copiesOrdered);
        m.put("copiesReceived",  copiesReceived);
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Outreach
    // ─────────────────────────────────────────────────────
    private Map<String, Object> outreachStats(LocalDate from, LocalDate to, String zone) {
        long outreachReports = countOutreach(from, to, zone);
        long photosSubmitted = sumOutreachMedia(from, to, zone);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("outreachReports",  outreachReports);
        m.put("photosSubmitted",  photosSubmitted);
        return m;
    }

    // ─────────────────────────────────────────────────────
    // Query helpers — zone_weekly_reports
    // ─────────────────────────────────────────────────────
    private long countReports(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    private long countDistinctZones(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .map(r -> r.getZoneName())
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    private long sumNewPartners(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getNewPartnersRecruited() != null ? r.getNewPartnersRecruited() : 0)
                .sum();
    }

    private BigDecimal sumRemittance(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .map(r -> r.getTotalPartnershipRemittance() != null ? r.getTotalPartnershipRemittance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long sumTestimonies(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getTestimoniesSubmitted() != null ? r.getTestimoniesSubmitted() : 0)
                .sum();
    }

    private long countApprovedZonal(LocalDate from, LocalDate to, String zone) {
        return reportRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && "APPROVED".equals(r.getStatus())
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    // ─────────────────────────────────────────────────────
    // Query helpers — partnership_reports
    // ─────────────────────────────────────────────────────
    private long countPartnership(LocalDate from, LocalDate to, String zone) {
        return prRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    private BigDecimal sumPartnershipRemittance(LocalDate from, LocalDate to, String zone) {
        return prRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .map(r -> r.getTotalRemittance() != null ? r.getTotalRemittance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ─────────────────────────────────────────────────────
    // Query helpers — testimonial_reports
    // ─────────────────────────────────────────────────────
    private long countTestimonials(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    private long sumTestimonialCount(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getTestimoniesCount() != null ? r.getTestimoniesCount() : 0)
                .sum();
    }

    private long sumTestimonialMedia(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> (r.getBeforeImages() != null ? r.getBeforeImages() : 0)
                              + (r.getAfterImages() != null  ? r.getAfterImages()  : 0))
                .sum();
    }

    private long sumTestimonialDocs(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getDocuments() != null ? r.getDocuments() : 0)
                .sum();
    }

    private long countDistinctZonesTestimonials(LocalDate from, LocalDate to, String zone) {
        return trRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .map(r -> r.getZoneName())
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    // ─────────────────────────────────────────────────────
    // Query helpers — magazine_reports
    // ─────────────────────────────────────────────────────
    private long countMagazine(LocalDate from, LocalDate to, String zone) {
        return mrRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    private long sumMagazineOrdered(LocalDate from, LocalDate to, String zone) {
        return mrRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getOrdered() != null ? r.getOrdered() : 0)
                .sum();
    }

    private long sumMagazineReceived(LocalDate from, LocalDate to, String zone) {
        return mrRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getReceived() != null ? r.getReceived() : 0)
                .sum();
    }

    // ─────────────────────────────────────────────────────
    // Query helpers — outreach_reports
    // ─────────────────────────────────────────────────────
    private long countOutreach(LocalDate from, LocalDate to, String zone) {
        return orRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .count();
    }

    private long sumOutreachMedia(LocalDate from, LocalDate to, String zone) {
        return orRepo.findAll().stream()
                .filter(r -> r.getSubmittedDate() != null
                          && !r.getSubmittedDate().isBefore(from)
                          && !r.getSubmittedDate().isAfter(to)
                          && (zone == null || zone.equalsIgnoreCase(r.getZoneName())))
                .mapToLong(r -> r.getMediaCount() != null ? r.getMediaCount() : 0)
                .sum();
    }

    // ─────────────────────────────────────────────────────
    // Generic approved counter (works on any list with getStatus())
    // ─────────────────────────────────────────────────────
    private <T> long countApproved(java.util.List<T> list, LocalDate from, LocalDate to, String zone) {
        return list.stream()
                .filter(r -> {
                    try {
                        var sd = (LocalDate) r.getClass().getMethod("getSubmittedDate").invoke(r);
                        var st = (String)    r.getClass().getMethod("getStatus").invoke(r);
                        var zn = (String)    r.getClass().getMethod("getZoneName").invoke(r);
                        return sd != null && !sd.isBefore(from) && !sd.isAfter(to)
                            && "APPROVED".equals(st)
                            && (zone == null || zone.equalsIgnoreCase(zn));
                    } catch (Exception e) { return false; }
                })
                .count();
    }
}
