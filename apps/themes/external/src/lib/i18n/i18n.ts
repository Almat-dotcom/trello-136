import { useI18n as useI18nBase } from "keycloakify";
import { en } from "./en";
import { ru } from "./ru";
import { kz } from "./kz";

type Props = Omit<Parameters<typeof useI18nBase>[0], "extraMessages">;

export function useI18n(props: Props) {
    const { kcContext } = props;
    return useI18nBase({
        kcContext,
        "extraMessages": {
            "en": en,
            "ru": ru,
            "kz": kz
        },
    });
}

export type I18n = NonNullable<ReturnType<typeof useI18n>>;