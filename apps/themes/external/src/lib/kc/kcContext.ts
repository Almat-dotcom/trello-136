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

type KcContextLoginOtp = KcContextBase.LoginOtp & {
    pageId: "login-otp.ftl";
      otpLogin? : {
        userOtpCredentials?: {
            id: string;
            userLabel: string;
        }[];
        selectedCredentialId?: string;
    }
};

type KcContextLoginConfigTotp = KcContextBase.LoginConfigTotp & {
    pageId: "login-config-totp.ftl";
    totp?:{
        supportedApplications: string[];
        totpSecret: string;
        totpSecretEncoded: string;
        totpSecretQrCode: string;
        qrUrl:string;
        manualUrl:string;
         policy:{
            type: "totp" | "hotp";
            algorithm:  "HmacSHA1" | "HmacSHA256" | "HmacSHA512";
            digits: number;
            period?: number;
            initialCounter?: number;
         };
    }
    isAppInitiatedAction?: boolean;
};

type KcContextLogoutConfirm = KcContextBase.LogoutConfirm & {
    pageId: "logout-confirm.ftl"
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
    KcContextLoginOtp |
    KcContextLoginConfigTotp |
    KcContextLogoutConfirm;

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
    //  "mockPageId": "login-otp.ftl",

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
                "currentLanguageTag": "ru",
                "supported": [{
                    "url": "mockurl-kz",
                    "label": "locale_kz",
                    "languageTag": "kz"
                }]
            },
            "totp": {
                "supportedApplications": ["Authenticator App", "Google Authenticator"],
                "totpSecret": "123456",
                "totpSecretEncoded": "123456",
                "totpSecretQrCode": "qrCode",
                "qrUrl":"#",
                "manualUrl":"#",
                "policy": {
                    "type":"totp",
                   "algorithm":  "HmacSHA1" ,
                    "digits": 6,
                    "period": 30,
                     "initialCounter": 0
                }
            },
             "isAppInitiatedAction": true
		},
		{
			"pageId": "update-email.ftl",
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
			"pageId": "update-phone.ftl",
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
            "pageId":"login-otp.ftl",
            "locale": {
                "currentLanguageTag": "ru",
                "supported": [{
                    "url": "mockurl-kz",
                    "label": "locale_kz",
                    "languageTag": "kz"
                }]
            },
             "otpLogin": {
                 "userOtpCredentials":[
                     {
                         "id": "otp_id",
                         "userLabel": "userLabel"
                     },
                     {
                         "id":"otp_id_2",
                         "userLabel": "userLabel_2"
                     }
                 ],
                 "selectedCredentialId": "otp_id"
            }
        }
	]
});

export type KcContext = NonNullable<typeof kcContext>;