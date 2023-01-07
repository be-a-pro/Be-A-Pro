import logo from './logo.svg';
import './App.css';
import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import axios from 'axios';
import Main from './component/Main';
import ProjectWrite from './component/project/ProejctWrite';
import Index from './component/Index';
import ProjectList from './component/project/ProjectList';
import Header from './component/Header';
import ScrollToTop from './hooks/ScrollToTop';
import ProejctDetail from './component/project/ProjectDetail';
import Signup from './component/signup/Signup';
import { useRef } from 'react';
import $ from 'jquery';
import jquery from 'jquery';
import Unknown from './component/test/Unknown';
import GlobalStyles from './component/GlobalStyles';

function App() {

  return (
    <BrowserRouter>
      <div className="App">
        {/* <Header /> 공통 컴포넌트인 HEADER 컴포넌트 */}
        <ScrollToTop /> {/* 새로고침 시, 최상단으로 스크롤 이동하는 커스텀 컴포넌트 */}
        <GlobalStyles />
        <Routes>
          {/* <Route path='/' element={<Main/>}></Route> ,  기존 리액트 시작 화면 */}
          <Route path='/authnaver' element={<Unknown />}></Route>
          <Route element={<Header />}>
            <Route path='/' element={<Index />}></Route> , {/* 기존 리액트 시작 화면에서 메인 라우터로 변경 */}
            <Route path='/projectList' element={<ProjectList />}></Route> , {/* 프로젝트 리스트 라우터 */}
            <Route path='/projectWrite' element={<ProjectWrite />}></Route> , {/* 프로젝트 작성 라우터 */}
            <Route path='/projectDetail' element={<ProejctDetail />}></Route>
            <Route path='/signup' element={<Signup />}></Route>
          </Route>
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
