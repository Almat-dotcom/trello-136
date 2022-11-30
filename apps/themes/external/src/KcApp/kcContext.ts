import { getKcContext } from "keycloakify/lib/getKcContext";

export const { kcContext } = getKcContext({
    // "mockPageId": "login.ftl",
	"mockPageId": "register.ftl",

    "mockData": [
        {
            "pageId": "login.ftl",
            "locale": {
                "currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-ru",
					"label": "Русский",
					"languageTag": "ru"
				},{
					"url": "mockurl-en",
					"label": "English",
					"languageTag": "en"
				},{
					"url": "mockurl-kz",
					"label": "Qazaq",
					"languageTag": "kz"
				}]
            },
			"auth": {
				"showResetCredentials": true,
			},
			"realm": {
				"registrationAllowed": true,
				"rememberMe": true
			},
			"message": {
				"type": "error",
				// eslint-disable-next-line no-template-curly-in-string
				"summary": "Some message"
			}
        },
		{
			"pageId": "register.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-ru",
					"label": "Русский",
					"languageTag": "ru"
				},{
					"url": "mockurl-en",
					"label": "English",
					"languageTag": "en"
				},{
					"url": "mockurl-kz",
					"label": "Qazaq",
					"languageTag": "kz"
				}]
			}
		}
    ]
});

export type KcContext = NonNullable<typeof kcContext>;