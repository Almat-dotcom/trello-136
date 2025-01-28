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
    KcContextBase.IdpReviewUserProfile | ExtendedRegister | ChangeEmail | ChangePhone;

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
});

export type KcContext = NonNullable<typeof kcContext>;