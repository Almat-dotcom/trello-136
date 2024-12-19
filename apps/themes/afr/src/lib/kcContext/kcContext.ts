import { getKcContext } from "keycloakify/lib/getKcContext";

export const { kcContext } = getKcContext({
	// Uncomment to test the login page for development.
	mockPageId: "login.ftl",
	mockData: [
		{
			pageId: "login.ftl",
			locale: {
				currentLanguageTag: "ru",
				supported: [{
					url: "mockurl-ru",
					label: "Русский",
					languageTag: "ru"
				},
				{
					url: "mockurl-kz",
					label: "kz",
					languageTag: "kz"
				},
				{
					url: "mockurl-en",
					label: "English",
					languageTag: "en"
				}]
			},
			realm: {
				displayNameHtml: 'AFR',
				registrationAllowed: false
			},
			message: {
				type: 'success'
			},
			messagesPerField: {
				existsError: (name: string) => true,
				get: (name: string) => "Error!",
				printIfExists: (name: string, t: string) => "Error!"
			}
		}
	]
});

export type KcContext = NonNullable<typeof kcContext>;