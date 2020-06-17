import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;

import java.util.*;

public class Data implements Iterable<List<Object>> {

    public static String[] fieldNames = {"OCNAME", "NODE1", "ALARMHEADER", "EVENTTIME", "CLEARANCETIMESTAMP"};
    private List<List<Object>> data;

    public Data() {
        data = new LinkedList<>();
    }

    public void appendRow(Map<String, Object> map) {
        if(map == null || map.size() == 0)
            return;

        List<Object> dataRow = new LinkedList<>();
        for(String field : fieldNames)
            dataRow.add(map.get(field));
        data.add(dataRow);
    }

    public void appendRow(Row excelRow) {
        if(excelRow == null)
            return;

        List<Object> dataRow = new LinkedList<>();
        for(Cell cell : excelRow) {
            CellType type = cell.getCellType();
            if(type == CellType.STRING)
                dataRow.add(cell.getStringCellValue());
            else if(type == CellType.NUMERIC)
                dataRow.add(new DateTime(cell.getDateCellValue()));
        }
        data.add(dataRow);
    }

    public List<List<Object>> rows() {
        return data;
    }

    public int getRowSize() {
        return data.size();
    }

    public int getColSize() {
        if(data != null && data.size() > 0 && data.get(0) != null)
            return data.get(0).size();
        return 0;
    }

    @Override
    public Iterator<List<Object>> iterator() {
        return data.iterator();
    }

}
