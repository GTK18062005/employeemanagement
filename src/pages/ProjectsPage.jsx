import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  Briefcase,
  ListChecks,
  Loader2,
  AlertCircle,
  CheckCircle2,
  Clock,
} from 'lucide-react';

const statusColor = (status) => {
  if (!status) return 'bg-gray-100 text-gray-700';
  const s = status.toUpperCase();
  if (s === 'IN_PROGRESS') return 'bg-sky-50 text-sky-700';
  if (s === 'PLANNING') return 'bg-amber-50 text-amber-700';
  if (s === 'COMPLETED') return 'bg-emerald-50 text-emerald-700';
  return 'bg-gray-100 text-gray-700';
};

const ProjectsPage = () => {
  const { user } = useAuth();
  const username = user?.username;

  const [loading, setLoading] = useState(true);
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const fetchData = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');
      setMessage('');

      const [projectsRes, tasksRes] = await Promise.all([
        api.get(`/projects/user/${username}`),
        api.get(`/projects/user/${username}/tasks`),
      ]);

      const projectList = projectsRes.data.projects || [];
      const taskList = tasksRes.data.tasks || [];

      setProjects(projectList);
      setTasks(taskList);

      if (projectList.length > 0 && !selectedProjectId) {
        setSelectedProjectId(projectList[0].id);
      }
    } catch (e) {
      console.error('PROJECTS ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Failed to load projects and tasks. Please refresh.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const selectedProject =
    projects.find((p) => p.id === selectedProjectId) || null;

  const tasksForSelectedProject = selectedProject
    ? tasks.filter((t) => t.projectId === selectedProject.id)
    : [];

  const completionAvg =
    tasksForSelectedProject.length > 0
      ? Math.round(
          tasksForSelectedProject.reduce(
            (sum, t) => sum + (t.completionPercentage ?? 0),
            0
          ) / tasksForSelectedProject.length
        )
      : 0;

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-purple-100 flex items-center justify-center">
              <Briefcase className="w-6 h-6 text-purple-600" />
            </div>
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
                Projects & Tasks
              </h1>
              <p className="text-gray-600 mt-1">
                View all your projects and their tasks in detail.
              </p>
            </div>
          </div>
        </div>

        {/* Messages */}
        {message && (
          <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-2xl text-sm">
            <CheckCircle2 className="w-4 h-4" />
            <span>{message}</span>
          </div>
        )}
        {error && (
          <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-2xl text-sm">
            <AlertCircle className="w-4 h-4" />
            <span>{error}</span>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Projects list */}
          <motion.div
            whileHover={{ y: -3, scale: 1.01 }}
            className="card p-4 lg:col-span-1"
          >
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Briefcase className="w-5 h-5 text-purple-600" />
                <p className="text-sm font-semibold text-gray-900">
                  My projects
                </p>
              </div>
              <span className="text-xs text-gray-500">
                {projects.length} total
              </span>
            </div>

            {projects.length === 0 ? (
              <p className="text-sm text-gray-500">
                You are not assigned to any projects yet.
              </p>
            ) : (
              <div className="space-y-2 max-h-[420px] overflow-y-auto pr-1">
                {projects.map((project) => (
                  <button
                    key={project.id}
                    type="button"
                    onClick={() => setSelectedProjectId(project.id)}
                    className={
                      'w-full text-left rounded-2xl px-3 py-2 border transition ' +
                      (selectedProjectId === project.id
                        ? 'border-purple-400 bg-purple-50'
                        : 'border-gray-200 hover:border-purple-300 hover:bg-gray-50')
                    }
                  >
                    <p className="text-sm font-semibold text-gray-900">
                      {project.projectName}
                    </p>
                    <p className="text-xs text-gray-500 mt-0.5">
                      Code: {project.projectCode} • Client:{' '}
                      {project.clientName}
                    </p>
                    <p className="text-xs text-gray-500 mt-0.5">
                      Manager: {project.projectManager}
                    </p>
                    <div className="flex items-center justify-between mt-1">
                      <span
                        className={
                          'inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold ' +
                          statusColor(project.status)
                        }
                      >
                        {project.status}
                      </span>
                      <span className="flex items-center gap-1 text-[11px] text-gray-500">
                        <Clock className="w-3 h-3" />
                        {project.startDate} → {project.endDate}
                      </span>
                    </div>
                  </button>
                ))}
              </div>
            )}
          </motion.div>

          {/* Selected project details + tasks */}
          <motion.div
            whileHover={{ y: -3, scale: 1.01 }}
            className="card p-4 lg:col-span-2 space-y-4"
          >
            {selectedProject ? (
              <>
                {/* Project details */}
                <div className="border border-gray-200 rounded-2xl p-4 bg-gray-50">
                  <div className="flex items-center justify-between mb-2">
                    <h2 className="text-lg font-semibold text-gray-900">
                      {selectedProject.projectName}
                    </h2>
                    <span
                      className={
                        'inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold ' +
                        statusColor(selectedProject.status)
                      }
                    >
                      {selectedProject.status}
                    </span>
                  </div>
                  <p className="text-sm text-gray-600 mb-2">
                    {selectedProject.description}
                  </p>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm text-gray-600">
                    <p>
                      <span className="font-semibold">Project code:</span>{' '}
                      {selectedProject.projectCode}
                    </p>
                    <p>
                      <span className="font-semibold">Client:</span>{' '}
                      {selectedProject.clientName}
                    </p>
                    <p>
                      <span className="font-semibold">Manager:</span>{' '}
                      {selectedProject.projectManager}
                    </p>
                    <p>
                      <span className="font-semibold">Priority:</span>{' '}
                      {selectedProject.priority}
                    </p>
                    <p>
                      <span className="font-semibold">Budget:</span>{' '}
                      ₹{Number(selectedProject.budget).toLocaleString('en-IN')}
                    </p>
                    <p>
                      <span className="font-semibold">Timeline:</span>{' '}
                      {selectedProject.startDate} → {selectedProject.endDate}
                    </p>
                    <p className="sm:col-span-2">
                      <span className="font-semibold">Tech stack:</span>{' '}
                      {selectedProject.technologyStack}
                    </p>
                  </div>
                </div>

                {/* Tasks */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <ListChecks className="w-5 h-5 text-blue-600" />
                      <p className="text-sm font-semibold text-gray-900">
                        Tasks for this project
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-xs text-gray-500">
                        Average progress
                      </p>
                      <p className="text-lg font-semibold text-gray-900">
                        {completionAvg}%
                      </p>
                    </div>
                  </div>

                  {tasksForSelectedProject.length === 0 ? (
                    <p className="text-sm text-gray-500">
                      No tasks assigned to you for this project.
                    </p>
                  ) : (
                    <div className="space-y-2 max-h-[320px] overflow-y-auto pr-1">
                      {tasksForSelectedProject.map((task) => (
                        <div
                          key={task.id}
                          className="rounded-2xl border border-gray-200 bg-white px-3 py-2"
                        >
                          <div className="flex items-center justify-between">
                            <p className="text-sm font-semibold text-gray-900">
                              {task.taskName}
                            </p>
                            <span
                              className={
                                'inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold ' +
                                statusColor(task.status)
                              }
                            >
                              {task.status}
                            </span>
                          </div>
                          <p className="text-xs text-gray-500 mt-0.5">
                            {task.description}
                          </p>
                          <div className="flex items-center justify-between mt-2">
                            <div className="flex-1 mr-3">
                              <div className="h-1.5 w-full rounded-full bg-gray-200 overflow-hidden">
                                <div
                                  className="h-1.5 rounded-full bg-blue-500"
                                  style={{
                                    width: `${Math.min(
                                      100,
                                      task.completionPercentage ?? 0
                                    )}%`,
                                  }}
                                />
                              </div>
                              <p className="text-[11px] text-gray-500 mt-1">
                                Progress:{' '}
                                <span className="font-semibold text-gray-800">
                                  {task.completionPercentage ?? 0}%
                                </span>
                              </p>
                            </div>
                            <p className="text-[11px] text-gray-500 text-right">
                              Due: <span className="font-semibold">{task.dueDate}</span>
                              <br />
                              Est: {task.estimatedHours}h • Act:{' '}
                              {task.actualHours}h
                            </p>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </>
            ) : (
              <p className="text-sm text-gray-500">
                Select a project from the left to view its details and tasks.
              </p>
            )}
          </motion.div>
        </div>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">Loading projects...</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectsPage;
