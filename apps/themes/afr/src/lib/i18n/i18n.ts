import { useI18n as useI18nBase } from "keycloakify";

type Props = Omit<Parameters<typeof useI18nBase>[0], "extraMessages">;

export function useI18n(props: Props) {
    const { kcContext } = props;
    return useI18nBase({
        kcContext,
        "extraMessages": {
            "en": {
                "ncaSignInProgress": "Работа с NCALayer.",
                "ncaSignFinished": "Запрос успешно подписан.",
                "ncaCancelled": "Вы отменили подписание запроса.",
                "ncaConnectionLost": "Не удается подключится к NCALayer. Проверьте запущен ли он на вашем устройстве.",
                "ncaError": "Ошибка при подписании запроса."
            },
            "ru": {
                "ncaSignInProgress": "Работа с NCALayer.",
                "ncaSignFinished": "Запрос успешно подписан.",
                "ncaCancelled": "Вы отменили подписание запроса.",
                "ncaConnectionLost": "Не удается подключится к NCALayer. Проверьте запущен ли он на вашем устройстве.",
                "ncaError": "Ошибка при подписании запроса."
            },
            "kz": {
                "ncaSignInProgress": "Работа с NCALayer.",
                "ncaSignFinished": "Запрос успешно подписан.",
                "ncaCancelled": "Вы отменили подписание запроса.",
                "ncaConnectionLost": "Не удается подключится к NCALayer. Проверьте запущен ли он на вашем устройстве.",
                "ncaError": "Ошибка при подписании запроса."
            }
        },
    });
}

export type I18n = NonNullable<ReturnType<typeof useI18n>>;
