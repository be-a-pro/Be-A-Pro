import React from 'react'
import ModalFrame from './ModalFrame'

export default function ProjectDelete() {
    return (
        <ModalFrame>
            <span className="applyTab">프로젝트를 삭제하시겠습니까?</span>
            <div className='deleteDescription'>
                <span>삭제된 정보는 다시 불러오실 수 없습니다.</span>
                <span>정말 삭제하시겠습니까?</span>
            </div>
            <div className='deleteBtnArea'>
                <button className="btn NO">예</button>
                <button className="btn OK">아니요</button>
            </div>
        </ModalFrame>
    )
}
