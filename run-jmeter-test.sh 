#!/bin/bash

echo "=== JMeter Performance Test ==="
echo ""

# Create results directory
mkdir -p jmeter-results

echo "1. Starting Spring Boot application..."
mvn spring-boot:run &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 25

echo "2. Running JMeter performance test..."
# Replace with your actual JMeter path
JMETER_PATH="/Applications/apache-jmeter-5.6.2/bin/jmeter"

if [ -f "$JMETER_PATH" ]; then
    $JMETER_PATH -n -t UserManagementTest.jmx -l jmeter-results/results.jtl
else
    echo "JMeter not found at $JMETER_PATH"
    echo "Please update the JMETER_PATH variable in this script"
    echo "Or run JMeter manually with:"
    echo "jmeter -n -t UserManagementTest.jmx -l jmeter-results/results.jtl"
fi

echo "3. Generating HTML report..."
if [ -f "$JMETER_PATH" ]; then
    $JMETER_PATH -g jmeter-results/results.jtl -o jmeter-results/dashboard
else
    echo "Skipping HTML report generation - JMeter not found"
fi

echo "4. Stopping Spring Boot application..."
kill $APP_PID 2>/dev/null

echo ""
echo "=== Test Complete ==="
echo "Results saved in jmeter-results/"
echo "Open jmeter-results/dashboard/index.html for detailed report"