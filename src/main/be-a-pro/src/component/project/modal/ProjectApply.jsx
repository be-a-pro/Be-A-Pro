import React from 'react'
import ModalFrame from './ModalFrame'
import './Modal.scss';
import { useState } from 'react';

export default function ProjectApply() {
    const [value, setValue] = useState("");
    const [listCheck, setCheck] = useState(false);
    const lst = [
        { id: 1, position: "서비스 기획", current: "0", clear: "1" }, { id: 2, position: "UXUI 디자인", current: "1", clear: "1" },
        { id: 3, position: "IOS", current: "0", clear: "1" }, { id: 4, position: "안드로이드", current: "0", clear: "1" }, { id: 5, position: "마케팅", current: "0", clear: "1" }
    ];
    const ClickList = (e) => {
        setCheck(!listCheck);
        setValue(e.target.value);
    }

    const ClickInput = (event) => {
        event.stopPropagation();
        setCheck(!listCheck);
    }
    return (
        <ModalFrame setClick={setCheck} setCheck={setCheck} listCheck={listCheck}>
            <span className="applyTab">프로젝트 지원하기</span>
            <span className="title">현우의 두근두근 비어 프로젝트</span>
            {/* <select className="applyList">
                <option value="" disabled selected>지원 분야를 선택해주세요</option>
                <option>UXUI 디자인</option>
                <option>개발</option>
                <option>서비스 기획</option>
            </select> */}
            <input className="applyList" onClick={ClickInput} defaultValue={value} placeholder='지원 분야를 선택해주세요' />
            {listCheck === true ? <div className="applyContent">
                {lst.map((item) => {
                    return (
                        <button onClick={ClickList} className={item.current === item.clear && "clearBtn"} key={item.id} value={item.position} disabled={item.current === item.clear && true}>
                            {item.position}<span>{item.current}/{item.clear}</span>
                        </button>
                    )
                })}
                {/* <button>서비스 기획 <span>0/1</span></button>
                <button>UXUI 디자인 <span>1/2</span></button>
                <button>IOS <span>0/2</span></button>
                <button>안드로이드 <span>1/2</span></button>
                <button>마케팅 <span>1/1</span></button> */}
            </div> : null}

            <div className="description">
                <span>* 개인정보보호를 위해 개인정보가 포함된 파일은 사전동의 없이 삭제될 수 있습니다.</span>
                <div className="description2">
                    <span>* 전화번호 및 포트폴리오는 <span>공개 여부와 상관없이 <span>프로젝트 팀장에게</span> 공개됩니다.</span></span>
                    <span>공개에 동의할 경우에만 ‘지원하기’ 버튼을 클릭해 주세요.</span>
                    <span>동의하지 않을 경우 프로젝트 지원이 불가능합니다.</span>
                </div>
            </div>
            <button className="btn OK">
                지원하기
            </button>
        </ModalFrame>
    )
}
