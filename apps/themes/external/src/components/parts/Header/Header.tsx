import { I18n } from "lib/i18n";
import Lang from "../Lang";
import { ReactComponent as Logo } from './logo.svg';
import FAQ from "./faq.svg";

const Header = ({ current, langs, i18n }: { current: string, langs: any[], i18n: I18n }) => {
    const { msgStr } = i18n;
    return (
        <>
            <div className="fixed flex items-center justify-between z-50 w-full bg-white border-b-2 border-gray-200">
                <div className="my-6 pl-4 xl:pl-72">
                    <Logo />
                </div>
                <div className="flex items-center pr-4 xl:pr-72">
                    <a className="flex items-center mr-4 text-dark-text hover:text-primary-focus" href="/foo">
                        <img className="inline-block mr-2" alt="faq" src={FAQ}/><span className="hidden md:inline-block">{msgStr("faq")}</span>
                    </a>
                    <Lang current={current} langs={langs} i18n={i18n} />
                </div>
            </div>
        </>
    );
}

export default Header;
