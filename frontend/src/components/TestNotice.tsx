export type TestNoticeKind = 'warning' | 'error';

export type TestNoticeData = {
  kind: TestNoticeKind;
  message: string;
};

export function TestNotice({ kind, message }: TestNoticeData) {
  return (
    <p className={`test-notice test-notice--${kind}`} role={kind === 'error' ? 'alert' : 'status'}>
      <span className="notice-icon" aria-hidden="true">
        {kind === 'error' ? '!' : 'i'}
      </span>
      {message}
    </p>
  );
}
