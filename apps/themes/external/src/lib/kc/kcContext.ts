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
			phoneNumber?: string;
			bin?: string;
			iin?: string;
		};
	};
}

type ChangeEmail = KcContextBase.Common & {
	pageId: "update-email.ftl";
	email?: string;
}

type ChangePhone = KcContextBase.Common & {
	pageId: "update-phone.ftl";
	phoneNumber?: string
}

type ExtendedContextExtended = KcContextBase.Login | KcContextBase.RegisterUserProfile |
 KcContextBase.Info | KcContextBase.Error | KcContextBase.LoginResetPassword | 
 KcContextBase.LoginVerifyEmail | KcContextBase.Terms  | KcContextBase.LoginUsername | 
 KcContextBase.WebauthnAuthenticate | KcContextBase.LoginPassword | KcContextBase.LoginUpdatePassword |
  KcContextBase.LoginUpdateProfile | KcContextBase.LoginIdpLinkConfirm | KcContextBase.LoginIdpLinkEmail |
   KcContextBase.LoginPageExpired  | KcContextBase.LogoutConfirm | KcContextBase.UpdateUserProfile |
    KcContextBase.IdpReviewUserProfile | ExtendedRegister | ChangeEmail | ChangePhone | 
	KcContextBase.KcContextExtendedLoginConfigTotp;

export const { kcContext } = getKcContext<ExtendedContextExtended>({

	// "mockPageId": "login.ftl",
	// "mockPageId": "register.ftl",
	// "mockPageId": "login-verify-email.ftl",
	// "mockPageId": "login-update-password.ftl",
	// "mockPageId": "logout-confirm.ftl",
	// "mockPageId": "login-reset-password.ftl",
	// "mockPageId": "login-page-expired.ftl",
	// "mockPageId": "info.ftl",
	// "mockPageId": "error.ftl",
	// "mockPageId": "update-email.ftl",
	// "mockPageId": "update-phone.ftl",
	// "mockPageId": "login-otp.ftl",
	"mockPageId": "login-config-totp.ftl",

	"mockData": [
		{
			"pageId": "login.ftl",
			"locale": {
				"currentLanguageTag": "kz",
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
				"currentLanguageTag": "ru",
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
		},
		{
			"pageId": "login-config-totp.ftl",
			"locale": {
			  "currentLanguageTag": "kz",
			  "supported": [
				{
				  "url": "mockurl-kz",
				  "label": "locale_kz",	
				  "languageTag": "kz"
				}
			  ]
			},
			"totp": {
			  "supportedApplications": ["App1", "App2"],
			  "totpSecret": "123456",
			  "totpSecretEncoded": "MTIzNDU2",
			  "totpSecretQrCode": "base64ImageString",
			  "qrUrl": "https://example.com/qr",
			  "manualUrl": "otpauth://totp/AccountName?secret=123456",
			  "policy": {
				"type": "totp",
				"algorithm": "HmacSHA256",
				"digits": 6,
				"period": 30
			  }
			},
			"isAppInitiatedAction": false
		  }  
	]
});

export type KcContext = NonNullable<typeof kcContext>;