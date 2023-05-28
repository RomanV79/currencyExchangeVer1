package Utils;

public class AlertMessage {
    public static final String MESSAGE_CURRENCY_ALREADY_EXIST = """
            {
                "message": "Currency already exist"
            }""";

    public static final String MESSAGE_ERROR_WITH_WORK_BY_DATABASE = """
            {
                "message": "Error by calling to database"
            }""";

    public static final String MESSAGE_ERROR_CORRECT_FILL_FIELD = """
            {
                "message": "Error while filling in fields"
            }""";

    public static final String MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST = """
            {
                "message": "Currency doesn't exist"
            }""";

    public static String MESSAGE_ERROR = """
            {
                "message": %s "
            }
            """;

    public static String MESSAGE_ERROR_CURRENCY_IS_NOT_VALID = """
            {
                "message": "Currency is not valid"
            }""";

    public static String MESSAGE_ERROR_RATE_IS_NOT_VALID = """
            {
                "message": "Rate is not valid"
            }""";

    public static String MESSAGE_ERROR_EXCHANGE_RATE_DOES_NOT_EXIST = """
            {
                "message": "Exchange rate doesn't exist"
            }""";

}
