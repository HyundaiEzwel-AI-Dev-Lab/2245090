<template>
  <div class="page">
    <div class="page-title">
      <div>
        <h1>시스템 관리</h1>
        <p>POC 기준 사용자/권한/공통코드 영역의 진입 화면입니다.</p>
      </div>
    </div>

    <div class="grid grid-3">
      <BaseCard title="사용자관리" description="임직원/외주 계정 관리 영역입니다.">
        <p>현재 POC는 Mock 사용자 기준으로 동작합니다.</p>
      </BaseCard>
      <BaseCard title="프로그램관리" description="시스템/업무/화면 정보를 관리합니다.">
        <p>요구사항과 WBS의 기준 정보로 확장 예정입니다.</p>
      </BaseCard>
      <BaseCard title="공통코드관리" description="상태, 업무유형, 테스트 차수 등을 관리합니다.">
        <p>운영 전환 시 별도 API로 분리합니다.</p>
      </BaseCard>
    </div>
  </div>
</template>
