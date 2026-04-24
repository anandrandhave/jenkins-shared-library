import argparse
from fpdf import FPDF
from datetime import datetime

parser = argparse.ArgumentParser()
parser.add_argument('--jobName')
parser.add_argument('--buildNum')
parser.add_argument('--branch')
parser.add_argument('--env')
args = parser.parse_args()

pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", 'B', 16)

# Header
pdf.set_fill_color(44, 62, 80)
pdf.set_text_color(255, 255, 255)
pdf.cell(190, 10, "Build Information", 1, 1, 'L', True)

# Build Info Table
pdf.set_text_color(0, 0, 0)
pdf.set_font("Arial", '', 12)
data = [
    ["Job Name", args.jobName],
    ["Build Number", args.buildNum],
    ["Branch", args.branch],
    ["Environment", args.env],
    ["Generated", datetime.now().strftime("%Y-%m-%d %H:%M:%S")]
]

for row in data:
    pdf.cell(95, 10, row[0], 1)
    pdf.cell(95, 10, row[1], 1, 1)

# Summary Section
pdf.ln(10)
pdf.set_fill_color(46, 204, 113)
pdf.set_text_color(255, 255, 255)
pdf.set_font("Arial", 'B', 16)
pdf.cell(190, 10, "Test Summary", 1, 1, 'L', True)

pdf.set_text_color(0, 0, 0)
pdf.set_font("Arial", '', 12)
pdf.cell(95, 10, "Total Tests", 1)
pdf.cell(95, 10, "2", 1, 1) # Static for now, can be parsed from XML
pdf.cell(95, 10, "Passed", 1)
pdf.cell(95, 10, "2", 1, 1)

pdf.output("test-report.pdf")
