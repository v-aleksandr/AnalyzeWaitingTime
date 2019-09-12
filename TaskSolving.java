import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Objects.isNull;

public class TaskSolving {
    public static void main(String[] args) throws ParseException {
        WaitTimeStatistics waitTimeStatistics = new WaitTimeStatistics();
        Scanner scanner = new Scanner(System.in);
        int count = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < count; i++) {
            waitTimeStatistics.add(scanner.nextLine());
        }
        waitTimeStatistics.printAnswers();
    }
}

class WaitTimeStatistics {
    private List<Statistics> statistics = new LinkedList<>();
    private List<String> answers = new LinkedList<>();

    public void add(String line) throws ParseException {
        char ch = line.charAt(0);
        switch (ch) {
            case 'C':
                statistics.add(new Statistics(line));
                break;
            case 'D':
                answers.add(answerQueryLine(line));
                break;
            default:
                break;
        }
    }

    public void printAnswers() {
        for (String answer : answers) {
            System.out.println(answer);
        }
    }

    private String answerQueryLine(String line) throws ParseException {
        Query query = new Query(line);
        int count = 0;
        int wholeWaitingTime = 0;
        for (Statistics stat : statistics) {
            if (stat.match(query)) {
                wholeWaitingTime += stat.getWaitingTime();
                count++;
            }
        }
        return count > 0 ? String.valueOf(wholeWaitingTime / count) : "-";
    }
}

class Query {
    static SimpleDateFormat myDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    int serviceId;
    int serviceVariationId;
    int questionTypeId;
    int questionTypeCategoryId;
    int questionTypeSubCategoryId;
    char responseType;
    Date responseDateFrom;
    Date responseDateTo;

    public Query(String line) throws ParseException {
        String[] list = line.split(" ");
        String[] splittedList = list[1].split("\\.");
        if (!splittedList[0].equals("*")) {
            serviceId = Integer.parseInt(splittedList[0]);
            if (splittedList.length > 1) serviceVariationId = Integer.parseInt(splittedList[1]);
        }
        splittedList = list[2].split("\\.");
        if (!splittedList[0].equals("*")) {
            questionTypeId = Integer.parseInt(splittedList[0]);
            if (splittedList.length > 1) questionTypeCategoryId = Integer.parseInt(splittedList[1]);
            if (splittedList.length > 2) questionTypeSubCategoryId = Integer.parseInt(splittedList[2]);
        }
        responseType = list[3].charAt(0);
        splittedList = list[4].split("-");
        responseDateFrom = myDateFormat.parse(splittedList[0]);
        if (splittedList.length > 1) responseDateTo = myDateFormat.parse(splittedList[1]);
    }
}

class Statistics {
    static SimpleDateFormat myDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private int serviceId;
    private int serviceVariationId;
    private int questionTypeId;
    private int questionTypeCategoryId;
    private int questionTypeSubCategoryId;
    private char responseType;
    private Date responseDate;
    private int waitingTime;

    public Statistics(String line) throws ParseException {
        String[] list = line.split(" ");
        String[] splittedList = list[1].split("\\.");
        serviceId = Integer.parseInt(splittedList[0]);
        if (splittedList.length > 1) serviceVariationId = Integer.parseInt(splittedList[1]);
        splittedList = list[2].split("\\.");
        questionTypeId = Integer.parseInt(splittedList[0]);
        if (splittedList.length > 1) questionTypeCategoryId = Integer.parseInt(splittedList[1]);
        if (splittedList.length > 2) questionTypeSubCategoryId = Integer.parseInt(splittedList[2]);
        responseType = list[3].charAt(0);
        responseDate = myDateFormat.parse(list[4]);
        waitingTime = Integer.parseInt(list[5]);
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public boolean match(Query query) {
        return serviceMatch(query) && questionTypeMatch(query) && responseTypeMatch(query) && responseDateMatch(query);
    }

    private boolean serviceMatch(Query query) {
        return query.serviceId == 0 ||
                (serviceId == query.serviceId &&
                        (query.serviceVariationId == 0 ||
                                serviceVariationId == query.serviceVariationId));
    }

    private boolean questionTypeMatch(Query query) {
        return query.questionTypeId == 0 ||
                (questionTypeId == query.questionTypeId &&
                        (query.questionTypeCategoryId == 0 ||
                                (questionTypeCategoryId == query.questionTypeCategoryId &&
                                        (query.questionTypeSubCategoryId == 0 ||
                                                questionTypeSubCategoryId == query.questionTypeSubCategoryId))));
    }

    private boolean responseTypeMatch(Query query) {
        return responseType == query.responseType;
    }

    private boolean responseDateMatch(Query query) {
        if (isNull(query.responseDateTo)) return responseDate == query.responseDateFrom;
        return responseDate.getTime() >= query.responseDateFrom.getTime() && responseDate.getTime() <= query.responseDateTo.getTime();
    }


}