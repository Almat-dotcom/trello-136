import { useI18n as useI18nBase } from "keycloakify";

type Props = Omit<Parameters<typeof useI18nBase>[0], "extraMessages">;

export function useI18n(props: Props) {
    const { kcContext } = props;
    return useI18nBase({
        kcContext,
        "extraMessages": {
            "en": {
                "alphanumericalCharsOnly": "Only alphanumerical characters",
                "doForgotPassword": "I forgot my password",
                "clientType": "Client type",
                "doCheckClientType": "Choose client type",
                "physical": "Physical client",
                "legal": "Legal client",
                "legalRole": "Role",
                "doCheckRole": "Choose role",
                "head": "Head",
                "employee": "Emplyee",
                "doEnterLastName": "Enter your last name",
                "doEnterFirstName": "Enter your first name",
                "middleName": "Middle Name",
                "doEnterMiddleName": "Enter your middle name",
                "doEnterEmail": "some@example.com",
                "bin": "BIN",
                "doEnterBin": "Enter BIN of your organization",
                "iin": "IIN",
                "doEnterIin": "Enter your IIN",
                "loginAccountTitle": "Sign in to your account",
                "registerTitle": "Register"
            },
            "ru": {
                "alphanumericalCharsOnly": "Допустимы только буквы и цифры",
				"doForgotPassword": "Я забыл свой пароль",
                "clientType": "Тип клиента",
                "doCheckClientType": "Выберите тип клиента",
                "physical": "Физическое лицо",
                "legal": "Юридическое лицо",
                "legalRole": "Роль",
                "doCheckRole": "Выбрать роль",
                "head": "Первый руководитель",
                "employee": "Сотрудник",
                "doEnterLastName": "Введите свою фамилию",
                "doEnterFirstName": "Введите свое имя",
                "middleName": "Отчество",
                "doEnterMiddleName": "Введите свое отчество",
                "doEnterEmail": "some@example.com",
                "bin": "БИН",
                "doEnterBin": "Введите БИН организации",
                "iin": "ИИН",
                "doEnterIin": "Введите Ваш ИИН",
                "loginAccountTitle": "Войти в свой аккаунт",
                "registerTitle": "Регистрация"
            },
            "kz": {
                "alphanumericalCharsOnly": "Буквы и цифры на кз",
                "doForgotPassword": "Я забыл свой пароль на кз",
                "clientType": "Тип клиента",
                "doCheckClientType": "Выберите тип клиента",
                "physical": "Физическое лицо",
                "legal": "Юридическое лицо",
                "legalRole": "Роль",
                "doCheckRole": "Выбрать роль",
                "head": "Первый руководитель",
                "employee": "Сотрудник",
                "doEnterLastName": "Введите свою фамилию",
                "doEnterFirstName": "Введите свое имя",
                "middleName": "Отчество",
                "doEnterMiddleName": "Введите свое отчество",
                "doEnterEmail": "some@example.com",
                "bin": "БИН",
                "doEnterBin": "Введите БИН организации",
                "iin": "ИИН",
                "doEnterIin": "Введите Ваш ИИН",
                "loginAccountTitle": "Войти в свой аккаунт",
                "registerTitle": "Регистрация"
            }
        },
    });
}

export type I18n = NonNullable<ReturnType<typeof useI18n>>;