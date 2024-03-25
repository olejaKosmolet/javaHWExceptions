
/*Напишите приложение, которое будет запрашивать у пользователя следующие данные, разделенные пробелом:

Фамилия Имя Отчество дата _ рождения номер _ телефона пол

Форматы данных:

фамилия, имя, отчество - строки
дата _ рождения - строка формата dd.mm.yyyy
номер _ телефона - целое беззнаковое число без форматирования
пол - символ латиницей f или m.

Приложение должно проверить введенные данные по количеству. Если количество не совпадает, вернуть код ошибки,
обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.

Приложение должно распарсить полученную строку и выделить из них требуемые значения. Если форматы данных не совпадают,
нужно бросить исключение, соответствующее типу проблемы. Можно использовать встроенные типы java и создать свои.
Исключение должно быть корректно обработано, пользователю выведено сообщение с информацией, что именно неверно.

Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну строку должны
записаться полученные данные, вида
<Фамилия> <Имя> <Отчество> <дата _ рождения> <номер _ телефона> <пол>

Однофамильцы должны записаться в один и тот же файл, в отдельные строки.
Не забудьте закрыть соединение с файлом.
При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано, пользователь должен
увидеть стектрейс ошибки.

17.12.1997 123456 m Ermolaev Oleg Nikolaevich
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Calendar;

public class HW {

    public static void main(String[] args) {
        while (true) {
            try {
                processUserData();
                break; // Если данные обработаны успешно, выходим из цикла
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

        String output = surname + " " + firstName + " " + middleName + " " + format.format(dateOfBirth) + " " + phoneNumber + " " + gender;

        try {
            File file = new File("peopleData/" + surname + ".txt");
            if (file.createNewFile()) {
                System.out.println("Файл создан");
            } else {
                System.out.println("Файл уже существует");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
            e.printStackTrace();
        }

        try(FileWriter writer = new FileWriter("peopleData/" + surname + ".txt", true))
        {
                writer.write(output);

            writer.write("\n");
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        finally {
            FileInputStream fin = null;
            try {

                if (fin != null)
                    fin.close();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }
}