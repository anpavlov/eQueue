# coding=utf-8
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import ttfonts, pdfmetrics
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from PIL import Image as PILImage
import qrcode
import qrcode.image.svg

from io import BytesIO
import os

times_path = os.path.normpath(os.path.join(os.path.dirname(__file__), './times.ttf'))
MyFontObject = ttfonts.TTFont('Arial', times_path)
pdfmetrics.registerFont(MyFontObject)


def gen_pdf(queue_name, qid):
    pdf_io = BytesIO()

    doc = SimpleDocTemplate(pdf_io, pagesize=A4)
    story = []

    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER, fontName='Arial'))
    p1t = '<font size=20>Очередь</font>'
    p2t = '<font size=24>«%s»</font>' % queue_name
    p3t = '<font size=20>ID очереди: %d</font>' % qid

    story.append(Paragraph(p1t, styles["Center"]))
    story.append(Spacer(1, 16))
    story.append(Paragraph(p2t, styles["Center"]))
    story.append(Spacer(1, 18))
    story.append(Paragraph(p3t, styles["Center"]))
    story.append(Spacer(1, 20))

    qr = qrcode.make('http://equeue/{}'.format(qid))
    qrim = BytesIO()
    qr.save(qrim)
    qrim.seek(0)
    im = Image(qrim, 15*cm, 15*cm)
    story.append(im)

    doc.build(story)
    return pdf_io

if __name__ == '__main__':
    # Test
    pdf = gen_pdf("My super queue", 143)
    with open("my.pdf", 'w') as f:
        f.write(pdf.getvalue())
