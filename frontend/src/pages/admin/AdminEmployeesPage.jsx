import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus } from 'lucide-react';
import Alert from '../../components/ui/Alert';
import Button from '../../components/ui/Button';
import DataTable from '../../components/ui/DataTable';
import PageHeader from '../../components/ui/PageHeader';
import CreateEmployeeForm from '../../components/admin/CreateEmployeeForm';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import { ADMIN_ROUTES } from '../../constants/routes';
import {
  createEmployee,
  getAllEmployees,
} from '../../services/employeeService';
import { useToast } from '../../context/ToastContext';
import { getErrorMessage } from '../../utils/apiError';
import { getEmployeeFullName } from '../../utils/formatters';

function AdminEmployeesPage() {
  const toast = useToast();
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const loadEmployees = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const data = await getAllEmployees();
      setEmployees(data);
    } catch (loadError) {
      setEmployees([]);
      setError(getErrorMessage(loadError, 'Failed to load employees.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadEmployees();
  }, [loadEmployees]);

  async function handleCreateEmployee(formData) {
    setSubmitting(true);
    try {
      await createEmployee(formData);
      toast.success('Employee created successfully.');
      setShowCreateForm(false);
      await loadEmployees();
    } catch (createError) {
      throw createError;
    } finally {
      setSubmitting(false);
    }
  }

  const columns = [
    { key: 'employeeCode', header: 'Code' },
    {
      key: 'name',
      header: 'Name',
      render: (employee) => getEmployeeFullName(employee) || '—',
    },
    { key: 'email', header: 'Email' },
    { key: 'department', header: 'Department', render: (e) => e.department || '—' },
    { key: 'designation', header: 'Designation', render: (e) => e.designation || '—' },
    {
      key: 'actions',
      header: 'Actions',
      render: (employee) => (
        <Link
          to={`${ADMIN_ROUTES.EMPLOYEES}/${employee.id}`}
          className="text-link"
        >
          View Details
        </Link>
      ),
    },
  ];

  return (
    <div className="page">
      <PageHeader title="Employees" description="Manage employee records.">
        <Button icon={Plus} onClick={() => setShowCreateForm(true)}>
          Create Employee
        </Button>
      </PageHeader>

      {error ? (
        <Alert variant="error" title="Error" onDismiss={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      {loading ? (
        <LoadingState title="Loading employees" description="Fetching employee records." />
      ) : error && !employees.length ? (
        <ErrorState description={error} onRetry={loadEmployees} />
      ) : (
        <DataTable
          columns={columns}
          data={employees}
          emptyTitle="No employees found"
          emptyDescription="Create an employee to get started."
        />
      )}

      {showCreateForm ? (
        <CreateEmployeeForm
          title="Create Employee"
          submitLabel="Create Employee"
          onSubmit={handleCreateEmployee}
          onCancel={() => setShowCreateForm(false)}
          submitting={submitting}
        />
      ) : null}
    </div>
  );
}

export default AdminEmployeesPage;
