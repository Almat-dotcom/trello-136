import { useI18n as useI18nBase } from "keycloakify";

type Props = Omit<Parameters<typeof useI18nBase>[0], "extraMessages">;

export function useI18n(props: Props) {
    const { kcContext } = props;
    return useI18nBase({
        kcContext,
        "extraMessages": {
            "en": {
                "alphanumericalCharsOnly": "Only alphanumerical characters",
                "doForgotPassword": "I forgot my password"
            },
            "ru": {
                "alphanumericalCharsOnly": "Допустимы только буквы и цифры",
				"doForgotPassword": "Я забыл свой пароль"
            },
            "kz": {
                "alphanumericalCharsOnly": "Буквы и цифры на кз",
                "doForgotPassword": "Я забыл свой пароль на кз"
            }
        },
    });
}

export type I18n = NonNullable<ReturnType<typeof useI18n>>;