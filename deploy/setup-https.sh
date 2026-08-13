#!/bin/bash
# =============================================================
# HTTPS Setup Script for Reporting Portal
# Server: 65.2.153.58 (EC2 - Ubuntu/Amazon Linux)
# Run this script on the EC2 server as root or with sudo
# =============================================================

set -e

echo "========================================="
echo "  HTTPS Setup for Reporting Portal"
echo "========================================="

# -----------------------------------------------------------
# Step 1: Install Nginx (if not already installed)
# -----------------------------------------------------------
echo ""
echo "[1/6] Installing Nginx..."
if command -v apt-get &> /dev/null; then
    # Ubuntu/Debian
    sudo apt-get update -y
    sudo apt-get install -y nginx openssl
elif command -v yum &> /dev/null; then
    # Amazon Linux / CentOS
    sudo yum install -y nginx openssl
    sudo amazon-linux-extras install nginx1 -y 2>/dev/null || true
fi

# -----------------------------------------------------------
# Step 2: Generate Self-Signed SSL Certificate
# -----------------------------------------------------------
echo ""
echo "[2/6] Generating self-signed SSL certificate..."
sudo openssl req -x509 -nodes -days 365 \
    -newkey rsa:2048 \
    -keyout /etc/ssl/private/selfsigned.key \
    -out /etc/ssl/certs/selfsigned.crt \
    -subj "/C=IN/ST=State/L=City/O=HealingSchool/OU=IT/CN=65.2.153.58"

echo "    Certificate generated (valid for 365 days)"

# -----------------------------------------------------------
# Step 3: Create web directory and deploy frontend build
# -----------------------------------------------------------
echo ""
echo "[3/6] Setting up frontend directory..."
sudo mkdir -p /var/www/reporting-portal

# If the build folder exists in the current directory, copy it
if [ -d "./build" ]; then
    echo "    Copying React build to /var/www/reporting-portal/"
    sudo cp -r ./build/* /var/www/reporting-portal/
elif [ -d "./frontend/build" ]; then
    echo "    Copying React build from frontend/build/"
    sudo cp -r ./frontend/build/* /var/www/reporting-portal/
else
    echo "    WARNING: No build folder found. You'll need to manually copy"
    echo "    your React build files to /var/www/reporting-portal/"
fi

sudo chown -R www-data:www-data /var/www/reporting-portal 2>/dev/null || \
sudo chown -R nginx:nginx /var/www/reporting-portal 2>/dev/null || true

# -----------------------------------------------------------
# Step 4: Install Nginx configuration
# -----------------------------------------------------------
echo ""
echo "[4/6] Configuring Nginx..."

# Backup existing config
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup 2>/dev/null || true

# Copy our site config
sudo cp ./deploy/nginx.conf /etc/nginx/sites-available/reporting-portal 2>/dev/null || \
sudo cp ./deploy/nginx.conf /etc/nginx/conf.d/reporting-portal.conf

# Enable the site (Ubuntu/Debian)
if [ -d "/etc/nginx/sites-enabled" ]; then
    sudo rm -f /etc/nginx/sites-enabled/default
    sudo ln -sf /etc/nginx/sites-available/reporting-portal /etc/nginx/sites-enabled/
fi

# Test Nginx config
echo "    Testing Nginx configuration..."
sudo nginx -t

# -----------------------------------------------------------
# Step 5: Open port 443 in firewall (if UFW is active)
# -----------------------------------------------------------
echo ""
echo "[5/6] Configuring firewall..."
if command -v ufw &> /dev/null; then
    sudo ufw allow 'Nginx Full' 2>/dev/null || true
    sudo ufw allow 443/tcp 2>/dev/null || true
    echo "    UFW rules updated"
else
    echo "    No UFW found - make sure port 443 is open in AWS Security Group!"
fi

# -----------------------------------------------------------
# Step 6: Start/Restart Nginx
# -----------------------------------------------------------
echo ""
echo "[6/6] Starting Nginx..."
sudo systemctl enable nginx
sudo systemctl restart nginx

echo ""
echo "========================================="
echo "  HTTPS Setup Complete!"
echo "========================================="
echo ""
echo "  Your site is now available at:"
echo "  https://65.2.153.58"
echo ""
echo "  IMPORTANT: Don't forget to open port 443"
echo "  in your AWS EC2 Security Group!"
echo ""
echo "  Security Group Inbound Rule to add:"
echo "  Type: HTTPS | Port: 443 | Source: 0.0.0.0/0"
echo ""
echo "  NOTE: Since this uses a self-signed certificate,"
echo "  browsers will show a security warning."
echo "  Click 'Advanced' > 'Proceed' to access the site."
echo "========================================="
