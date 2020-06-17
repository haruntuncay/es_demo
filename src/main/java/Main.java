public class Main {

    public static void main(String[] args) {
        Data data = ExcelReader.read("Book2.xlsx");
        Elastic.indexDataInBulk(data);
        Data elasticData = Elastic.getAll();
        ExcelWriter.write(elasticData, "hello.xlsx");
        Elastic.close();
    }
}
