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
                "username": "Username",
                "password": "Password",
                "rememberMe": "Remember Me",
                "doLogIn": "Sign In"
            },
            "ru": {
                "alphanumericalCharsOnly": "Допустимы только буквы и цифры",
				"doForgotPassword": "Я забыл свой пароль",
                "username": "Имя пользователя",
                "password": "Пароль",
                "rememberMe": "Запомни меня",
                "doLogIn": "Войти"
            },
            "kz": {
                "alphanumericalCharsOnly": "Буквы и цифры на кз",
                "doForgotPassword": "Я забыл свой пароль на кз",
                "username": "Аты",
                "password": "Пароль",
                "rememberMe": "Мені ұмытпаңыз",
                "doLogIn": "Кіру"
            }
        },
    });
}

export type I18n = NonNullable<ReturnType<typeof useI18n>>;