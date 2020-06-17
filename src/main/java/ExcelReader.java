import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelReader {

    public static Data read(String pathToExcel) {
        Data data = null;
        Workbook workbook = null;
        try (BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(pathToExcel))) {
            workbook = new XSSFWorkbook(inStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            // Skip title row.
            if (iterator.hasNext())
                iterator.next();
            // Build data.
            data = new Data();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.getPhysicalNumberOfCells() != Data.fieldNames.length)
                    continue;
                data.appendRow(row);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

}
