import { getKcContext } from "keycloakify/lib/getKcContext";

export const { kcContext } = getKcContext({
	// Uncomment to test the login page for development.
	mockPageId: "login.ftl",
	mockData: [
		{
			pageId: "login.ftl",
			locale: {

				currentLanguageTag: "ru",
			}
		}
	]
});

export type KcContext = NonNullable<typeof kcContext>;