import { getKcContext, KcContextBase } from "keycloakify/lib/getKcContext";

type ExtendedRegister = KcContextBase.RegisterCommon & {
	pageId: "register.ftl";
        register: {
            formData: {
				residency?: string;
				clientType?: string;
				legalRole?: string;
                firstName?: string;
				middleName?: string;
                displayName?: string;
                lastName?: string;
                email?: string;
				bin?: string;
                iin?: string;
            };
        };
}

type ExtendedContextExtended = KcContextBase.Login | KcContextBase.RegisterUserProfile | KcContextBase.Info | KcContextBase.Error | KcContextBase.LoginResetPassword | KcContextBase.LoginVerifyEmail | KcContextBase.Terms | KcContextBase.LoginOtp | KcContextBase.LoginUsername | KcContextBase.WebauthnAuthenticate | KcContextBase.LoginPassword | KcContextBase.LoginUpdatePassword | KcContextBase.LoginUpdateProfile | KcContextBase.LoginIdpLinkConfirm | KcContextBase.LoginIdpLinkEmail | KcContextBase.LoginPageExpired | KcContextBase.LoginConfigTotp | KcContextBase.LogoutConfirm | KcContextBase.UpdateUserProfile | KcContextBase.IdpReviewUserProfile | ExtendedRegister;

export const { kcContext } = getKcContext<ExtendedContextExtended>({

    // "mockPageId": "login.ftl",
	"mockPageId": "register.ftl",
	// "mockPageId": "login-verify-email.ftl",
	// "mockPageId": "login-update-password.ftl",
	// "mockPageId": "logout-confirm.ftl",
	// "mockPageId": "login-reset-password.ftl",
	// "mockPageId": "login-page-expired.ftl",
	// "mockPageId": "info.ftl",
	// "mockPageId": "error.ftl",

    "mockData": [
        {
            "pageId": "login.ftl",
            "locale": {
                "currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
            },
			"auth": {
				"showResetCredentials": true,
			},
			"realm": {
				"registrationAllowed": true,
				"rememberMe": true,
				"internationalizationEnabled": true
			},
			"message": {
				"type": "error",
				// eslint-disable-next-line no-template-curly-in-string
				"summary": "invalidUserMessage"
			}
        },
		{
			"pageId": "register.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
                "currentLanguageTag": "en",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
            }
		},
		{
			"pageId": "login-verify-email.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "login-update-password.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "logout-confirm.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "login-reset-password.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "login-page-expired.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "info.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		},
		{
			"pageId": "error.ftl",
			"realm": {
				"internationalizationEnabled": true
			},
			"locale": {
				"currentLanguageTag": "ru",
				"supported": [{
					"url": "mockurl-kz",
					"label": "locale_kz",
					"languageTag": "kz"
				}]
			}
		}
    ]
});

export type KcContext = NonNullable<typeof kcContext>;