import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HW {

    public static void main(String[] args) {
        while (true) {
            try {
                processUserData();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Неверный формат числа. " + e.getMessage());
                System.out.println("Пожалуйста, введите данные заново.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Пожалуйста, введите данные заново.");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void processUserData() throws IllegalArgumentException, ParseException, NumberFormatException {
        SimpleDateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        format.setLenient(false);
        String surname = "";
        String firstName = "";
        String middleName = "";
        Date dateOfBirth = null;
        long phoneNumber = 0;
        char gender = ' ';

        Scanner in = new Scanner(System.in);
        System.out.println("Введите данные: Фамилия Имя Отчество дата_рождения номер_телефона пол");
        String data = in.nextLine();

        String[] parts = data.split(" ");

        if (parts.length < 6) {
            throw new IllegalArgumentException("Неверное количество данных! Пожалуйста, " +
                    "добавьте недостающие параметры");
        }
        if (parts.length > 6) {
            throw new IllegalArgumentException("Неверное количество данных! Пожалуйста, " +
                    "удалите лишние параметры");
        }

        for (String part : parts) {
            if (Pattern.matches("\\d{2}\\.\\d{2}\\.\\d{4}", part)) {

                try {
                    if (dateOfBirth == null) {
                        dateOfBirth = format.parse(part);
                    } else {
                        throw new IllegalArgumentException("Дата рождения уже задана");
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Неверный формат даты рождения. Обязательно укажите " +
                            "правильный месяц, проверка месяца доступна в премиум версии. " +
                            "Пожалуйста, используйте формат dd.MM.yyyy");
                }
            }
            else if (Pattern.matches("\\d+", part)) {
                if (phoneNumber == 0) {
                    phoneNumber = Long.parseLong(part);
                } else {
                    throw new IllegalArgumentException("Вы ввели номер телефона несколько раз!");
                }
            } else if (Pattern.matches("[mf]", part)) {
                if (gender == ' ') {
                    gender = part.charAt(0);
                } else {
                    throw new IllegalArgumentException("Пол уже задан!");
                }
            } else {
                if (surname.isEmpty()) {
                    surname = part;
                } else if (firstName.isEmpty()) {
                    firstName = part;
                } else if (middleName.isEmpty()) {
                    middleName = part;
                }
            }
        }
        if (gender != 'm' && gender != 'f') {
            throw new IllegalArgumentException("Неправильный формат пола");
        }

        if (surname.isEmpty() || firstName.isEmpty() || middleName.isEmpty() || dateOfBirth == null ||
                phoneNumber == 0 || gender == ' ') {
            throw new IllegalArgumentException("Не все данные были введены");
        }

        if (!surname.matches("[A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException("Неправильный формат фамилии");
        }
        if (!firstName.matches("[A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException("Неправильный формат имени");
        }
        if (!middleName.matches("[A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException("Неправильный формат отчества");
        }

        String formattedDate = (dateOfBirth != null) ? format.format(dateOfBirth) : "N/A";

        String output = "<" + surname + "> <" + firstName + "> <" + middleName + "> <" + format.format(dateOfBirth) + "> <" + phoneNumber + "> <" + gender + ">";

        try {
            File file = new File("peopleData/" + surname + ".txt");
            if (file.createNewFile()) {
                System.out.println("Файл создан");
            } else {
                System.out.println("Файл уже существует");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
            e.printStackTrace(); // Выводим стектрейс ошибки
        }

        FileWriter writer = null; // Объявляем FileWriter здесь, чтобы быть уверенными в его закрытии

        try {
            writer = new FileWriter("peopleData/" + surname + ".txt", true);
            writer.write(output);
            writer.write("\n");
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close(); // Закрываем FileWriter в блоке finally
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}