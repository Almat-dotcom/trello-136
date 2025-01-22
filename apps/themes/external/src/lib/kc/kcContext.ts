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
	phoneNumber?: string;
};

type LoginConfigTotp = KcContextBase.Common & {
	pageId: "login-config-totp.ftl";
	mode: "qr" | "manual" | string; // Можете расширять при необходимости
	totp: {
	  totpSecretEncoded: string;
	  qrUrl: string;
	  policy: {
		supportedApplications: string[];
		algorithm: string;
		digits: number;
		lookAheadWindow: number;
		type: string;
		period: number;
	  };
	  totpSecretQrCode: string;
	  manualUrl: string;
	  totpSecret: string;
	  otpCredentials: Array<{
		id: string;
		userLabel: string;
	  }>;
	};
  };

type OtpForm = KcContextBase.Common & {
	pageId: "otp.ftl";
};

type ExtendedContextExtended =
	KcContextBase.Login |
	KcContextBase.RegisterUserProfile |
	KcContextBase.Info |
	KcContextBase.Error |
	KcContextBase.LoginResetPassword |
	KcContextBase.LoginVerifyEmail |
	KcContextBase.Terms |
	KcContextBase.LoginOtp |
	KcContextBase.LoginUsername |
	KcContextBase.WebauthnAuthenticate |
	KcContextBase.LoginPassword |
	KcContextBase.LoginUpdatePassword |
	KcContextBase.LoginUpdateProfile |
	KcContextBase.LoginIdpLinkConfirm |
	KcContextBase.LoginIdpLinkEmail |
	KcContextBase.LoginPageExpired |
	KcContextBase.LoginConfigTotp |
	KcContextBase.LogoutConfirm |
	KcContextBase.UpdateUserProfile |
	KcContextBase.IdpReviewUserProfile |
	ExtendedRegister |
	ChangeEmail |
	ChangePhone |
	LoginConfigTotp |
	OtpForm;

export const { kcContext } = getKcContext<ExtendedContextExtended>({
	// "mockPageId": "login.ftl",
	// "mockPageId": "register.ftl",
	"mockPageId":"login-config-totp.ftl",
	// "mockPageId": "login-verify-email.ftl",
	// "mockPageId": "login-update-password.ftl",
	// "mockPageId": "logout-confirm.ftl",
	// "mockPageId": "login-reset-password.ftl",
	// "mockPageId": "login-page-expired.ftl",
	// "mockPageId": "info.ftl",
	// "mockPageId": "error.ftl",
	// "mockPageId": "update-email.ftl",
	// "mockPageId": "update-phone.ftl",

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
			"mode": "qr",
			"totp": {
				"totpSecretEncoded": "JBSWY3DPEHPK3PXP",
				"qrUrl": "https://upload.wikimedia.org/wikipedia/commons/0/0b/QR_code_Wikimedia_Commons_%28URL%29.png",
				"policy": {
					"supportedApplications": ["Google Authenticator", "Authy"],
					"algorithm": "HmacSHA1",
					"digits": 6,
					"lookAheadWindow": 1,
					"type": "totp",
					"period": 30
				},
				"totpSecretQrCode": "https://upload.wikimedia.org/wikipedia/commons/0/0b/QR_code_Wikimedia_Commons_%28URL%29.png",
				"manualUrl": "otpauth://totp/example?secret=JBSWY3DPEHPK3PXP",
				"totpSecret": "JBSWY3DPEHPK3PXP",
				"otpCredentials": [
					{
						"id": "otp-1",
						"userLabel": "Primary"
					}
				]
			}
		},
	]
});

export type KcContext = NonNullable<typeof kcContext>;
