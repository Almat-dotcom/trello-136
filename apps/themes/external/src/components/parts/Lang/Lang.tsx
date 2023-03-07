import { I18n } from "lib/i18n";
import { useState } from "react";
import ExpandLess from "./expand_less.svg";
import ExpandMore from "./expand_more.svg";

const Choice = ({ label, url }: { label: string, url: string }) => (
    <div className="cursor-pointer w-full border-gray-100 rounded-t border-b text-dark-text">
        <div className="flex w-full items-center p-2 pl-2 border-transparent bg-white border-l-2 relative hover:text-primary-focus hover:border-primary-focus">
            <div className="w-full items-center flex">
                <a href={url} className="mx-2 leading-6">{label}</a>
            </div>
        </div>
    </div>
)

const Lang = ({ current, langs, i18n }: { current: string, langs: any[], i18n: I18n }) => {
    const selected = langs.filter((value) => value.languageTag === current)[0]
    const { msgStr } = i18n

    const [ expanded, setExpanded ] = useState(false)

    const displayed = langs.filter((value) => !!msgStr(value.languageTag))
    const choices = displayed.map((lang, i) => <Choice key={i} label={msgStr(lang.languageTag)} url={lang.url} />)

    return (
        <div className="w-28">
            <div className="flex flex-col items-center relative">
                <div className="w-full">
                    <div className="my-1 bg-transparent p-1 flex rounded">
                        <div className="flex flex-auto flex-wrap"></div>
                        <input value={msgStr(selected.languageTag)} onChange={() => {}} className="p-1 px-2 appearance-none outline-none w-full bg-transparent text-dark-text" readOnly />
                        <div>
                            <button className="cursor-pointer w-6 h-full flex items-center text-gray-400 outline-none focus:outline-none" onClick={() => setExpanded(!expanded)}>
                                <img alt="expand" src={expanded ? ExpandLess : ExpandMore}/>
                            </button>
                        </div>
                    </div>
                </div>
                <div className={ (expanded ? "absolute" : "hidden") + " shadow top-full z-40 w-full lef-0 rounded max-h-select overflow-y-auto" } >
                    <div className="flex flex-col w-full">
                        {choices}
                    </div>
                </div>
            </div>
        </div>
    )
};

export default Lang;
