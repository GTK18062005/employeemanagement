import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ArrowLeft, Pencil } from 'lucide-react';
import ProjectDetailsView from '../../components/projects/ProjectDetailsView';
import Alert from '../../components/ui/Alert';
import Button from '../../components/ui/Button';
import PageHeader from '../../components/ui/PageHeader';
import ProjectFormModal from '../../components/admin/ProjectFormModal';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import { ADMIN_ROUTES } from '../../constants/routes';
import { getAdminProjectById, updateAdminProject } from '../../services/projectService';
import { getAllEmployees } from '../../services/employeeService';
import { useToast } from '../../context/ToastContext';
import { getErrorMessage } from '../../utils/apiError';

function AdminProjectDetailsPage() {
  const toast = useToast();
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [projectManagers, setProjectManagers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showEditForm, setShowEditForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const loadProject = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const [data, employees] = await Promise.all([
        getAdminProjectById(projectId),
        getAllEmployees().catch(() => []),
      ]);
      setProject(data);
      setProjectManagers(employees);
    } catch (loadError) {
      setProject(null);
      setError(getErrorMessage(loadError, 'Failed to load project details.'));
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    loadProject();
  }, [loadProject]);

  async function handleUpdateProject(formData) {
    setSubmitting(true);
    try {
      const updated = await updateAdminProject(projectId, formData);
      setProject(updated);
      toast.success('Project updated successfully.');
      setShowEditForm(false);
    } catch (updateError) {
      throw updateError;
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page">
      <PageHeader title="Project Details" description="View and manage project information.">
        <Link to={ADMIN_ROUTES.PROJECTS}>
          <Button variant="secondary" icon={ArrowLeft} size="sm">
            Back to projects
          </Button>
        </Link>
        {project ? (
          <Button icon={Pencil} size="sm" onClick={() => setShowEditForm(true)}>
            Edit
          </Button>
        ) : null}
      </PageHeader>

      {error ? (
        <Alert variant="error" title="Error" onDismiss={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      {loading ? (
        <LoadingState title="Loading project" description="Fetching project details." />
      ) : error ? (
        <ErrorState description={error} onRetry={loadProject} />
      ) : project ? (
        <ProjectDetailsView project={project} />
      ) : null}

      {showEditForm && project ? (
        <ProjectFormModal
          title="Edit Project"
          submitLabel="Save Changes"
          initialValues={project}
          projectManagers={projectManagers}
          includeStatus
          onSubmit={handleUpdateProject}
          onCancel={() => setShowEditForm(false)}
          submitting={submitting}
        />
      ) : null}
    </div>
  );
}

export default AdminProjectDetailsPage;
