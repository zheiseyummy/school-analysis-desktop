package com.youlai.system.common.views;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/** 学科独立分析的正式 PDF 报告视图。 */
public class CourseAnalysisPDFView extends CustomAbstractPdfView {

    @Override
    protected Document newDocument() {
        return new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildPdfDocument(Map<String, Object> model, Document document,
                                    PdfWriter pdfWriter, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font normal = new Font(baseFont, 9);
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        Font small = new Font(baseFont, 8);

        Paragraph title = new Paragraph(String.valueOf(model.get("title")), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("范围：" + model.get("scopeName") + "    满分：" + model.get("fullScore"), normal));
        document.add(new Paragraph(" "));

        Map<String, Object> summary = (Map<String, Object>) model.get("summary");
        PdfPTable summaryTable = new PdfPTable(6);
        summaryTable.setWidthPercentage(100);
        addCell(summaryTable, "参考人数", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("参考人数")), normal, false);
        addCell(summaryTable, "有效成绩", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("有效成绩人数")), normal, false);
        addCell(summaryTable, "平均分", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("平均分")), normal, false);
        addCell(summaryTable, "最高分", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("最高分")), normal, false);
        addCell(summaryTable, "最低分", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("最低分")), normal, false);
        addCell(summaryTable, "及格率", normal, true);
        addCell(summaryTable, String.valueOf(summary.get("及格率")), normal, false);
        document.add(summaryTable);
        document.add(new Paragraph(" "));

        List<Map<String, Object>> rows = (List<Map<String, Object>>) model.get("rows");
        PdfPTable table = new PdfPTable(new float[]{0.7f, 1.2f, 1.3f, 1.3f, 1f, 1f, 1f});
        table.setWidthPercentage(100);
        String[] headers = {"排名", "学号", "姓名", "班级", "成绩", "状态", "得分率"};
        for (String header : headers) addCell(table, header, normal, true);
        for (Map<String, Object> row : rows) {
            addCell(table, value(row.get("rank")), small, false);
            addCell(table, value(row.get("studentCode")), small, false);
            addCell(table, value(row.get("studentName")), small, false);
            addCell(table, value(row.get("clazzName")), small, false);
            addCell(table, value(row.get("score")), small, false);
            addCell(table, value(row.get("statusLabel")), small, false);
            Object percent = row.get("percent");
            addCell(table, percent == null ? "-" : String.valueOf(percent) + "%", small, false);
        }
        document.add(table);
    }

    private void addCell(PdfPTable table, String text, Font font, boolean header) {
        PdfPCell cell = new PdfPCell(new Paragraph(text == null ? "" : text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        if (header) cell.setBackgroundColor(new BaseColor(224, 242, 254));
        table.addCell(cell);
    }

    private String value(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }
}
