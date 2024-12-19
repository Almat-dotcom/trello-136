import { getKcContext } from "keycloakify/lib/getKcContext";

export const { kcContext } = getKcContext({
	// Uncomment to test the login page for development.
	mockPageId: "login.ftl",
	mockData: [
		{
			pageId: "login.ftl",
			locale: {
				currentLanguageTag: "kz",
				supported: [{
					url: "mockurl-ru",
					label: "Русский",
					languageTag: "ru"
				},
				{
					url: "mockurl-kz",
					label: "Қазақ",
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
				type: 'error'
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