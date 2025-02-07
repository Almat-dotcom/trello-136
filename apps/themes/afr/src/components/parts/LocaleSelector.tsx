import React, { memo } from "react";
import { I18n } from "lib/i18n";
import { KcContextBase } from "keycloakify";

interface LocaleSelectorProps {
    locale: KcContextBase["locale"];
    i18n: I18n;
}

export const LocaleSelector = memo(({ locale, i18n }: LocaleSelectorProps) => {
    const { currentLanguageTag, supported } = locale!;
    const { advancedMsg } = i18n;

    const currentLang = supported.find(it => it.languageTag === currentLanguageTag);

    if (!currentLang) {
        return null;
    }

    return (
        <div id="kc-locale">
            <div id="kc-locale-wrapper" className="">
                <div className="kc-dropdown" id="kc-locale-dropdown">
                    <a href={currentLang.url} id="kc-current-locale-link">
                        {advancedMsg(currentLang.label)}
                    </a>
                    <ul>
                        {supported.map(it => (
                            <li key={it.languageTag} className="kc-dropdown-item">
                                <a href={it.url}>{advancedMsg(it.languageTag)}</a>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
});
