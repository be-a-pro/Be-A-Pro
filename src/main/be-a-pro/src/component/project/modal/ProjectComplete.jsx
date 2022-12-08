import React from 'react'
import ModalFrame from './ModalFrame'

export default function ProjectComplete() {
    return (
        <ModalFrame>
            <span className="applyTab">프로젝트 개설이 완료되었습니다!</span>
            <div className='completeDescription'>
                <span>내 프로젝트에 들어가서 지원자를 확인할 수 있습니다.</span>
                <span>이제 비어프로에서 다양한 사람들과 프로젝트를 진행해보세요 😊</span>
            </div>
            <button className='btn OK'>메인으로</button>
        </ModalFrame>
    )
}
