import React from 'react'
import ModalFrame from './ModalFrame'

export default function ProjectPrev() {
    return (
        <ModalFrame>
            <span className="applyTab">정말 뒤로 가시겠습니까?</span>
            <div className='prevDescription'>
                <span>페이지를 이탈하면 작정 중인  게시글은 저장되지 않습니다.</span>
            </div>
            <div className='deleteBtnArea'>
                <button className="btn NO">예</button>
                <button className="btn OK">아니요</button>
            </div>
        </ModalFrame>
    )
}
