#!/bin/sh
set -eu

storage_dir="${UPLOAD_DIR:-/var/data/embroidery/disenos}"
mkdir -p "$storage_dir"
# Named Docker volumes keep ownership from their first creation. Repair both the
# mount root and its existing files before the JVM drops root privileges.
chown -R appuser:appgroup "$storage_dir"
exec su-exec appuser:appgroup java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar
