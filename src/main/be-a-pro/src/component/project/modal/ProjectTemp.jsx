import React from 'react'
import ModalFrame from './ModalFrame'

export default function ProjectTemp() {
    return (
        <ModalFrame>
            <span className="applyTab">임시저장이 완료되었습니다.</span>
            <div className='tempDescription'>
                <span>임시저장된 프로젝트를 불러오시면 이어서 작성하실 수 있습니다.</span>
            </div>
            <button className='btn OK'>메인으로</button>
        </ModalFrame>
    )
}
