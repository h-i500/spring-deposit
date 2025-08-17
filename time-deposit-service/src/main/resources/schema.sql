-- 旧: CREATE TYPE IF NOT EXISTS deposit_status AS ENUM ('OPEN', 'CLOSED');

CREATE TABLE IF NOT EXISTS time_deposits (
  id UUID PRIMARY KEY,
  owner TEXT NOT NULL,
  principal NUMERIC(19,2) NOT NULL,
  annual_rate NUMERIC(9,6) NOT NULL,
  term_days INT NOT NULL,
  start_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  maturity_at TIMESTAMPTZ NOT NULL,
  -- 旧: status deposit_status NOT NULL DEFAULT 'OPEN'
  status TEXT NOT NULL DEFAULT 'OPEN',
  CONSTRAINT time_deposits_status_chk CHECK (status IN ('OPEN','CLOSED'))
);
