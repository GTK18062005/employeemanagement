import { useCallback, useEffect, useState } from 'react';
import AttendanceStatusBadge from '../../components/attendance/AttendanceStatusBadge';
import Alert from '../../components/ui/Alert';
import Button from '../../components/ui/Button';
import Card from '../../components/ui/Card';
import DataTable from '../../components/ui/DataTable';
import Input from '../../components/ui/Input';
import PageHeader from '../../components/ui/PageHeader';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import { ATTENDANCE_STATUSES } from '../../constants/attendanceStatus';
import {
  getEmployeeAttendance,
  getEmployeeAttendanceByDate,
  markEmployeeAttendance,
} from '../../services/attendanceService';
import { useToast } from '../../context/ToastContext';
import { getErrorMessage } from '../../utils/apiError';
import { formatDate, formatDateTime } from '../../utils/formatters';

function EmployeeAttendancePage() {
  const toast = useToast();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [lookupDate, setLookupDate] = useState('');
  const [lookupRecord, setLookupRecord] = useState(null);
  const [lookupError, setLookupError] = useState('');
  const [lookupLoading, setLookupLoading] = useState(false);
  const [form, setForm] = useState({ date: '', status: 'PRESENT' });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const loadAttendance = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const data = await getEmployeeAttendance();
      setRecords(data);
    } catch (loadError) {
      setRecords([]);
      setError(getErrorMessage(loadError, 'Failed to load attendance history.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAttendance();
  }, [loadAttendance]);

  function handleFormChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleMarkAttendance(event) {
    event.preventDefault();
    setFormError('');
    setSubmitting(true);

    try {
      await markEmployeeAttendance(form);
      toast.success('Attendance marked successfully.');
      setForm({ date: '', status: 'PRESENT' });
      await loadAttendance();
    } catch (submitError) {
      setFormError(getErrorMessage(submitError, 'Failed to mark attendance.'));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleLookup(event) {
    event.preventDefault();

    if (!lookupDate) {
      return;
    }

    setLookupLoading(true);
    setLookupError('');
    setLookupRecord(null);

    try {
      const data = await getEmployeeAttendanceByDate(lookupDate);
      setLookupRecord(data);
    } catch (lookupErr) {
      setLookupRecord(null);
      setLookupError(getErrorMessage(lookupErr, 'No attendance found for this date.'));
    } finally {
      setLookupLoading(false);
    }
  }

  const columns = [
    {
      key: 'date',
      header: 'Date',
      render: (record) => formatDate(record.date),
    },
    {
      key: 'status',
      header: 'Status',
      render: (record) => <AttendanceStatusBadge status={record.status} />,
    },
    {
      key: 'createdAt',
      header: 'Marked At',
      render: (record) => formatDateTime(record.createdAt),
    },
    {
      key: 'updatedAt',
      header: 'Updated',
      render: (record) => formatDateTime(record.updatedAt),
    },
  ];

  return (
    <div className="page">
      <PageHeader title="My Attendance" description="Mark attendance and review your history." />

      {error ? (
        <Alert variant="error" title="Error" onDismiss={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      <div className="dashboard-grid">
        <Card title="Mark Attendance" description="Submit attendance for a specific date.">
          <form className="stack-form" onSubmit={handleMarkAttendance}>
            <Input
              label="Date"
              name="date"
              type="date"
              value={form.date}
              onChange={handleFormChange}
              required
            />
            <div className="ui-input">
              <label htmlFor="status">Status</label>
              <select
                id="status"
                name="status"
                className="ui-input__field"
                value={form.status}
                onChange={handleFormChange}
                required
              >
                {ATTENDANCE_STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>
            {formError ? (
              <p className="form-error" role="alert">
                {formError}
              </p>
            ) : null}
            <Button type="submit" loading={submitting}>
              {submitting ? 'Submitting...' : 'Mark Attendance'}
            </Button>
          </form>
        </Card>

        <Card title="Lookup by Date" description="View attendance for a specific date.">
          <form className="stack-form" onSubmit={handleLookup}>
            <Input
              label="Date"
              name="lookupDate"
              type="date"
              value={lookupDate}
              onChange={(event) => setLookupDate(event.target.value)}
              required
            />
            <Button type="submit" variant="secondary" loading={lookupLoading}>
              {lookupLoading ? 'Searching...' : 'Lookup'}
            </Button>
          </form>
          {lookupError ? (
            <p className="form-error" role="alert" style={{ marginTop: '12px' }}>
              {lookupError}
            </p>
          ) : null}
          {lookupRecord ? (
            <dl className="detail-list lookup-result">
              <div className="detail-list__item">
                <dt>Date</dt>
                <dd>{formatDate(lookupRecord.date)}</dd>
              </div>
              <div className="detail-list__item">
                <dt>Status</dt>
                <dd>
                  <AttendanceStatusBadge status={lookupRecord.status} />
                </dd>
              </div>
            </dl>
          ) : null}
        </Card>
      </div>

      {loading ? (
        <LoadingState title="Loading attendance" description="Fetching your attendance history." />
      ) : error && !records.length ? (
        <ErrorState description={error} onRetry={loadAttendance} />
      ) : (
        <DataTable
          columns={columns}
          data={records}
          emptyTitle="No attendance records"
          emptyDescription="Mark your attendance to see history here."
        />
      )}
    </div>
  );
}

export default EmployeeAttendancePage;
