import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus } from 'lucide-react';
import ProjectStatusBadge from '../../components/projects/ProjectStatusBadge';
import Alert from '../../components/ui/Alert';
import Button from '../../components/ui/Button';
import DataTable from '../../components/ui/DataTable';
import PageHeader from '../../components/ui/PageHeader';
import ProjectFormModal from '../../components/admin/ProjectFormModal';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import { ADMIN_ROUTES } from '../../constants/routes';
import {
  createAdminProject,
  getAdminProjects,
} from '../../services/projectService';
import { getAllEmployees } from '../../services/employeeService';
import { useToast } from '../../context/ToastContext';
import { getErrorMessage } from '../../utils/apiError';
import { formatDate } from '../../utils/formatters';

function AdminProjectsPage() {
  const toast = useToast();
  const [projects, setProjects] = useState([]);
  const [projectManagers, setProjectManagers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const loadProjects = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const [projectsData, employees] = await Promise.all([
        getAdminProjects(),
        getAllEmployees().catch(() => []),
      ]);
      setProjects(projectsData);
      // Filter employees who could be project managers (all employees are potential managers)
      setProjectManagers(employees);
    } catch (loadError) {
      setProjects([]);
      setError(getErrorMessage(loadError, 'Failed to load projects.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProjects();
  }, [loadProjects]);

  async function handleCreateProject(formData) {
    setSubmitting(true);
    try {
      await createAdminProject(formData);
      toast.success('Project created successfully.');
      setShowCreateForm(false);
      await loadProjects();
    } catch (createError) {
      throw createError;
    } finally {
      setSubmitting(false);
    }
  }

  const columns = [
    { key: 'name', header: 'Project' },
    {
      key: 'status',
      header: 'Status',
      render: (project) => <ProjectStatusBadge status={project.status} />,
    },
    {
      key: 'managerName',
      header: 'Manager',
      render: (project) => project.managerName ?? '—',
    },
    {
      key: 'startDate',
      header: 'Start Date',
      render: (project) => formatDate(project.startDate),
    },
    {
      key: 'endDate',
      header: 'End Date',
      render: (project) => formatDate(project.endDate),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (project) => (
        <Link
          to={`${ADMIN_ROUTES.PROJECTS}/${project.id}`}
          className="text-link"
        >
          View Details
        </Link>
      ),
    },
  ];

  return (
    <div className="page">
      <PageHeader title="Projects" description="Manage organization projects.">
        <Button icon={Plus} onClick={() => setShowCreateForm(true)}>
          Create Project
        </Button>
      </PageHeader>

      {error ? (
        <Alert variant="error" title="Error" onDismiss={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      {loading ? (
        <LoadingState title="Loading projects" description="Fetching projects." />
      ) : error && !projects.length ? (
        <ErrorState description={error} onRetry={loadProjects} />
      ) : (
        <DataTable
          columns={columns}
          data={projects}
          emptyTitle="No projects found"
          emptyDescription="Create a project to get started."
        />
      )}

      {showCreateForm ? (
        <ProjectFormModal
          title="Create Project"
          submitLabel="Create Project"
          projectManagers={projectManagers}
          onSubmit={handleCreateProject}
          onCancel={() => setShowCreateForm(false)}
          submitting={submitting}
        />
      ) : null}
    </div>
  );
}

export default AdminProjectsPage;
