import React from 'react'
import ModalFrame from './ModalFrame'

export default function ProjectClear() {
    return (
        <ModalFrame>
            <span className="applyTab">삭제된 프로젝트입니다</span>
            <div className='clearDescription'>
                <span>프로젝트의 정보를 불러올 수 없어요 😢</span>
            </div>
            <button className='btn OK'>메인으로</button>
        </ModalFrame >
    )
}
