# Generate start script in target/start, so that sbt is not needed.
sbt start-script

# Set AVSL_CONFIG to point to the desired logging configuration.
export AVSL_CONFIG=avsl.config

# Start the server.
target/start
