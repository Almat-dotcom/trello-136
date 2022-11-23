import { getKcContext } from "keycloakify/lib/getKcContext";

export const { kcContext } = getKcContext({
    "mockPageId": "login.ftl",
	// "mockPageId": "register-user-profile.ftl",

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
			"pageId": "register-user-profile.ftl",
			"locale": {
				"currentLanguageTag": "ru"
			},
			"profile": {
				"attributes": [
					{
						"validators": {
							"pattern": {
								"pattern": "^[a-zA-Z0-9]+$",
								"ignore.empty.value": true,
								// eslint-disable-next-line no-template-curly-in-string
								"error-message": "${alphanumericalCharsOnly}",
							},
						},
						//NOTE: To override the default mock value
						"value": undefined,
						"name": "username"
					},
					{
						"validators": {
							"options": {
								"options": ["male", "female", "non-binary", "transgender", "intersex", "non_communicated"]
							}
						},
						// eslint-disable-next-line no-template-curly-in-string
						"displayName": "${gender}",
						"annotations": {},
						"required": true,
						"groupAnnotations": {},
						"readOnly": false,
						"name": "gender"
					}
				]
			}
		}
    ]
});

export type KcContext = NonNullable<typeof kcContext>;