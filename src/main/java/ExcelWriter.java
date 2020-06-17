import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    private static String DEFAULT_FILE_NAME = "out.xlsx";

    public static void write(Data data, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // Date cells require special formatting.
        CreationHelper helper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(helper.createDataFormat().getFormat("d-m-yy H:mm"));

        int rowNum = 0;
        // Write title row.
        Row titleRow = sheet.createRow(rowNum++);
        for(int cellNum = 0; cellNum < Data.fieldNames.length; cellNum++) {
            titleRow.createCell(cellNum).setCellValue(Data.fieldNames[cellNum]);
        }

        for(List<Object> dataRow : data) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            for(Object obj : dataRow) {
                Cell cell = row.createCell(cellNum++);
                if(obj instanceof String)
                    cell.setCellValue((String) obj);
                else if(obj instanceof Date) {
                    cell.setCellStyle(dateCellStyle);
                    cell.setCellValue((Date) obj);
                }
            }
        }

        // Auto-size all columns.
        for(int i = 0; i < data.getColSize(); i++)
            sheet.autoSizeColumn(i);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Data data) {
        write(data, DEFAULT_FILE_NAME);
    }
}
