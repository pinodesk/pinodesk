#!/bin/bash

# This script builds and publishes a DEB package.
# It can handle both 'snapshot' and 'release' builds.
#
# It expects the following environment variables to be set by the CI/CD system:
# - RELEASE_MODE: The mode of the build, either 'snapshot' or 'release'.
# - MAVEN_CLI_OPTS: Command line options for Maven.
# - FTP_USERNAME, FTP_PASSWORD, FTP_HOST: Credentials for the FTP server.
#
# For 'snapshot' mode:
# - APPLICATION_PROPERTIES_SNAPSHOT_DEB: Path to the snapshot application.properties file.
# - PINODESK_API_KEY_STAGING: API key for the staging Pinodesk API.
#
# For 'release' mode:
# - APPLICATION_PROPERTIES_RELEASE_DEB: Path to the release application.properties file.
# - PINODESK_API_KEY_PRODUCTION: API key for the production Pinodesk API.

set -euxo pipefail

# --- Validate Input ---
if [[ -z "${RELEASE_MODE}" ]]; then
  echo "Error: RELEASE_MODE environment variable is not set."
  echo "Please set it to 'snapshot' or 'release'."
  exit 1
fi

# --- Set Mode-Specific Variables ---
if [[ "${RELEASE_MODE}" == "snapshot" ]]; then
  echo "--- Running in SNAPSHOT mode ---"
  APP_PROPERTIES_FILE="${APPLICATION_PROPERTIES_SNAPSHOT_DEB}"
  FTP_TARGET_DIR="./domains/download.pinodesk.com/public_html/snapshots/linux-deb/"
  API_URL="https://api-staging.pinodesk.com/v1/releases"
  API_KEY="${PINODESK_API_KEY_STAGING}"
  DOWNLOAD_URL_BASE="https://download.pinodesk.com/snapshots/linux-deb/"
elif [[ "${RELEASE_MODE}" == "release" ]]; then
  echo "--- Running in RELEASE mode ---"
  APP_PROPERTIES_FILE="${APPLICATION_PROPERTIES_RELEASE_DEB}"
  FTP_TARGET_DIR="./domains/download.pinodesk.com/public_html/releases/linux-deb/"
  API_URL="https://api.pinodesk.com/v1/releases"
  API_KEY="${PINODESK_API_KEY_PRODUCTION}"
  DOWNLOAD_URL_BASE="https://download.pinodesk.com/releases/linux-deb/"
else
  echo "Error: Invalid RELEASE_MODE '${RELEASE_MODE}'. Must be 'snapshot' or 'release'."
  exit 1
fi

echo "--- Determining Project Version ---"
PROJECT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
echo "Project version: ${PROJECT_VERSION}"

echo "--- Preparing Configuration Files ---"
cp -rv "${APP_PROPERTIES_FILE}" src/main/resources/application.properties
cp -rv src/main/resources/logback.xml.example src/main/resources/logback.xml
rm -rv src/main/resources/*.example

echo "--- Building DEB Package ---"
mvn $MAVEN_CLI_OPTS clean package -P deb -DskipTests

echo "--- Preparing for Upload ---"
cd target
FILENAME_DEB=$(ls *.deb | head -n 1)
echo "DEB package filename: ${FILENAME_DEB}"

echo "--- Uploading to FTP Server ---"
lftp -c "set ftp:ssl-allow no; open -u $FTP_USERNAME,$FTP_PASSWORD $FTP_HOST; mirror -Rv --file=$FILENAME_DEB --target-directory=${FTP_TARGET_DIR}"

echo "--- Notifying Release API ---"
curl -v --location "${API_URL}" \
--header 'Content-Type: application/json' \
--header 'X-Pinodesk-Api-Key: '"${API_KEY}" \
--data '{
    "name": "Pinodesk for Linux DEB v'"$PROJECT_VERSION"'",
    "platform": "linux-deb",
    "version": "'"$PROJECT_VERSION"'",
    "download_url": "'"${DOWNLOAD_URL_BASE}${FILENAME_DEB}"'"
}'
