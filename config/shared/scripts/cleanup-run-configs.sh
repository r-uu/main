#!/bin/bash
# Script to clean up JasperReports run configurations

cd /home/r-uu/develop/github/main/.idea/runConfigurations

# Remove JasperReports configurations
rm -f jasper_githubco_InvoiceGenerator__exec_java_.xml
rm -f jasper_gemini_Main__Application_.xml

# Remove redundant docx4j exec config (we use Application config instead)
rm -f docx4j_Invoice_Generator__exec_java_.xml

echo "✓ JasperReports run configurations removed"
echo "✓ Redundant docx4j configurations removed"
echo ""
echo "Remaining configurations:"
ls -1 *.xml

