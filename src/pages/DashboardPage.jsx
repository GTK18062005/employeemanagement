import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  Clock,
  CalendarDays,
  Car,
  Wallet,
  Briefcase,
  Star,
  Activity,
  ArrowRight,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const DashboardPage = () => {
  const { user } = useAuth();
  const username = user?.username;
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [attendanceToday, setAttendanceToday] = useState(null);
  const [attendanceStats, setAttendanceStats] = useState(null);
  const [leaveBalance, setLeaveBalance] = useState(null);
  const [latestSalary, setLatestSalary] = useState(null);
  const [parkingAllocation, setParkingAllocation] = useState(null);
  const [parkingRequestStatus, setParkingRequestStatus] = useState(null);
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [avgRating, setAvgRating] = useState(null);
  const [error, setError] = useState('');

  const avatarLetter = (user?.name || user?.username || 'U')
    .toString()
    .charAt(0)
    .toUpperCase();

  useEffect(() => {
    if (!username) return;

    const fetchData = async () => {
      try {
        setLoading(true);
        setError('');

        const [
          todayAttendanceRes,
          attendanceStatsRes,
          leaveBalanceRes,
          latestSalaryRes,
          parkingAllocationRes,
          projectsRes,
          tasksRes,
          ratingRes,
        ] = await Promise.all([
          api.get(`/attendance/today/${username}`),
          api.get(`/attendance/stats/${username}`),
          api.get(`/leaves/balance/${username}`),
          api.get(`/salaries/user/${username}/latest`),
          api.get(`/parking/allocations/user/${username}`),
          api.get(`/projects/user/${username}`),
          api.get(`/projects/user/${username}/tasks`),
          api.get(`/performance/user/${username}/average-rating`),
        ]);

        setAttendanceToday(todayAttendanceRes.data.attendance || null);
        setAttendanceStats(attendanceStatsRes.data.stats || null);
        setLeaveBalance(leaveBalanceRes.data.balance || null);
        setLatestSalary(latestSalaryRes.data.salary || null);

        const parkingData = parkingAllocationRes.data || {};
        setParkingAllocation(parkingData.allocation || null);
        setParkingRequestStatus(parkingData.requestStatus || null);

        setProjects(projectsRes.data.projects || []);
        setTasks(tasksRes.data.tasks || []);
        setAvgRating(
          ratingRes.data.averageRating !== undefined &&
          ratingRes.data.averageRating !== null
            ? ratingRes.data.averageRating
            : null
        );
      } catch (err) {
        console.error(err);
        setError('Failed to load dashboard data. Please refresh.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [username]);

  const formatTime = (timeStr) =>
    !timeStr ? '--' : timeStr.toString().slice(0, 5);
  const formatDate = (dateStr) => (dateStr ? dateStr : '--');
  const formatMoney = (amount) =>
    !amount && amount !== 0
      ? '--'
      : `₹${Number(amount).toLocaleString('en-IN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })}`;

  const todayStatus =
    attendanceToday?.status || (attendanceToday ? 'PRESENT' : 'Not Marked');
  const workingHours = attendanceToday?.workingHours ?? 0;

  // Parking helpers
  const getParkingText = () => {
    if (parkingAllocation) {
      return (
        <>
          <p className="text-sm text-gray-500">
            Slot:{' '}
            <span className="font-semibold">
              {parkingAllocation.slotNumber}
            </span>
          </p>
          <p className="text-sm text-gray-500 mt-1">
            Vehicle:{' '}
            <span className="font-semibold">
              {parkingAllocation.vehicleNumber} ({parkingAllocation.vehicleType})
            </span>
          </p>
          <p className="text-sm text-gray-500 mt-1">
            Status:{' '}
            <span className="font-semibold text-emerald-600">
              {parkingAllocation.status}
            </span>
          </p>
          <p className="text-xs text-gray-500 mt-1">
            Valid until:{' '}
            <span className="font-semibold">
              {parkingAllocation.validUntil || 'No expiry'}
            </span>
          </p>
        </>
      );
    }

    if (parkingRequestStatus === 'PENDING') {
      return (
        <p className="text-gray-500 text-sm">
          Parking allocation request submitted. Waiting for approval. Click this card to view details.
        </p>
      );
    }

    if (parkingRequestStatus === 'REJECTED') {
      return (
        <p className="text-gray-500 text-sm">
          Your parking request was rejected. Click this card to request a new slot or contact admin.
        </p>
      );
    }

    return (
      <p className="text-gray-500 text-sm">
        You have no active parking allocation. Click this card to request a parking slot.
      </p>
    );
  };

  const getParkingBadgeText = () => {
    if (parkingAllocation) return 'Active';
    if (parkingRequestStatus === 'PENDING') return 'Requested';
    if (parkingRequestStatus === 'REJECTED') return 'Rejected';
    return 'Not active';
  };

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-7xl mx-auto space-y-8">
        {/* Header with profile on right */}
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-4xl sm:text-5xl font-bold bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">
              Dashboard
            </h1>
            <p className="text-gray-600 mt-2 text-lg">
              Welcome back,
              <span className="font-semibold"> {user?.name || user?.username}</span>
            </p>
          </div>

          <motion.div
            whileHover={{ scale: 1.02, y: -2 }}
            className="flex items-center space-x-3 bg-white/80 backdrop-blur-xl border border-gray-200/60 rounded-full px-3 py-2 shadow-sm"
          >
            <div className="hidden sm:flex flex-col items-end mr-1">
              <span className="text-sm font-semibold text-gray-900">
                {user?.name || user?.username}
              </span>
              <span className="text-xs text-gray-500 uppercase tracking-wide">
                {user?.role || 'STAFF'}
              </span>
            </div>

            {user?.profilePicture ? (
              <img
                src={user.profilePicture}
                alt="Profile"
                className="w-11 h-11 rounded-full object-cover border-2 border-blue-500 shadow-md"
              />
            ) : (
              <div className="w-11 h-11 rounded-full bg-gradient-to-br from-blue-500 via-purple-500 to-indigo-500 flex items-center justify-center text-white font-semibold text-lg shadow-md">
                {avatarLetter}
              </div>
            )}
          </motion.div>
        </div>

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-2xl">
            {error}
          </div>
        )}

        {/* Top row: Attendance + Stats + Leave + Performance */}
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
          {/* Attendance Today */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 flex flex-col justify-between"
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-blue-100 flex items-center justify-center">
                <Clock className="w-6 h-6 text-blue-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-blue-50 text-blue-700">
                Attendance
              </span>
            </div>
            <div>
              <p className="text-sm text-gray-500 mb-1">
                Today • {formatDate(attendanceToday?.attendanceDate)}
              </p>
              <p className="text-2xl font-bold text-gray-900 mb-1">
                {todayStatus}
              </p>
              <p className="text-sm text-gray-500">
                In:{' '}
                <span className="font-semibold">
                  {formatTime(attendanceToday?.checkInTime)}
                </span>{' '}
                · Out:{' '}
                <span className="font-semibold">
                  {formatTime(attendanceToday?.checkOutTime)}
                </span>
              </p>
              <p className="text-sm text-gray-500 mt-1">
                Working hours:{' '}
                <span className="font-semibold">
                  {workingHours || '--'} hrs
                </span>
              </p>
            </div>
          </motion.div>

          {/* Attendance Stats */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6"
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-indigo-100 flex items-center justify-center">
                <Activity className="w-6 h-6 text-indigo-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-indigo-50 text-indigo-700">
                Last 30 days
              </span>
            </div>
            <p className="text-sm text-gray-500 mb-1">Attendance Rate</p>
            <p className="text-3xl font-bold text-gray-900 mb-4">
              {attendanceStats?.attendancePercentage ?? 0}%
            </p>
            <div className="grid grid-cols-2 gap-2 text-sm text-gray-600">
              <div className="flex justify-between">
                <span>Present</span>
                <span className="font-semibold text-emerald-600">
                  {attendanceStats?.presentDays ?? 0}
                </span>
              </div>
              <div className="flex justify-between">
                <span>Absent</span>
                <span className="font-semibold text-red-500">
                  {attendanceStats?.absentDays ?? 0}
                </span>
              </div>
              <div className="flex justify-between">
                <span>Half Days</span>
                <span className="font-semibold text-amber-500">
                  {attendanceStats?.halfDays ?? 0}
                </span>
              </div>
              <div className="flex justify-between">
                <span>Leave</span>
                <span className="font-semibold text-blue-500">
                  {attendanceStats?.leaveDays ?? 0}
                </span>
              </div>
            </div>
          </motion.div>

          {/* Leave Balance */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 cursor-pointer"
            onClick={() => navigate('/leaves')}
          >
            <div className="flex items:center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center">
                <CalendarDays className="w-6 h-6 text-emerald-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-emerald-50 text-emerald-700">
                Leave Balance
              </span>
            </div>
            <div className="grid grid-cols-2 gap-3 text-sm">
              <div className="bg-emerald-50 rounded-2xl px-3 py-2">
                <p className="text-xs text-emerald-700">Sick</p>
                <p className="text-lg font-bold text-emerald-900">
                  {leaveBalance?.sickLeaves ?? 0}
                </p>
              </div>
              <div className="bg-blue-50 rounded-2xl px-3 py-2">
                <p className="text-xs text-blue-700">Casual</p>
                <p className="text-lg font-bold text-blue-900">
                  {leaveBalance?.casualLeaves ?? 0}
                </p>
              </div>
              <div className="bg-amber-50 rounded-2xl px-3 py-2">
                <p className="text-xs text-amber-700">Earned</p>
                <p className="text-lg font-bold text-amber-900">
                  {leaveBalance?.earnedLeaves ?? 0}
                </p>
              </div>
              <div className="bg-purple-50 rounded-2xl px-3 py-2">
                <p className="text-xs text-purple-700">Special</p>
                <p className="text-lg font-bold text-purple-900">
                  {(leaveBalance?.maternityLeaves ?? 0) +
                    (leaveBalance?.paternityLeaves ?? 0)}
                </p>
              </div>
            </div>
            <p className="mt-3 text-xs text-emerald-700 flex items-center gap-1">
              Manage leaves
              <ArrowRight className="w-3 h-3" />
            </p>
          </motion.div>

          {/* Performance Rating */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6"
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-yellow-100 flex items-center justify-center">
                <Star className="w-6 h-6 text-yellow-500" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-yellow-50 text-yellow-700">
                Performance
              </span>
            </div>
            <p className="text-sm text-gray-500 mb-1">Average Rating</p>
            <p className="text-3xl font-bold text-gray-900 mb-2">
              {avgRating ? avgRating.toFixed(2) : 'N/A'}{' '}
              <span className="text-lg text-yellow-500">/ 5.0</span>
            </p>
            <p className="text-xs text-gray-500">
              Based on manager reviews for your recent periods
            </p>
          </motion.div>
        </div>

        {/* Second row: Salary + Parking + Projects & Tasks */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Latest Salary */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 lg:col-span-1 cursor-pointer"
            onClick={() => navigate('/salary')}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-indigo-100 flex items-center justify-center">
                <Wallet className="w-6 h-6 text-indigo-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-indigo-50 text-indigo-700">
                Latest Salary
              </span>
            </div>
            {latestSalary ? (
              <>
                <p className="text-sm text-gray-500">
                  Month:{' '}
                  <span className="font-semibold">
                    {latestSalary.salaryMonth}
                  </span>
                </p>
                <p className="text-sm text-gray-500 mt-1">
                  Department:{' '}
                  <span className="font-semibold">
                    {latestSalary.department}
                  </span>
                </p>
                <p className="text-3xl font-bold text-gray-900 mt-4">
                  {formatMoney(latestSalary.netSalary)}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Gross: {formatMoney(latestSalary.grossSalary)}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Status:{' '}
                  <span className="font-semibold text-emerald-600">
                    {latestSalary.paymentStatus}
                  </span>
                </p>
                <p className="mt-3 text-xs text-indigo-700 flex items-center gap-1">
                  View full salary history
                  <ArrowRight className="w-3 h-3" />
                </p>
              </>
            ) : (
              <p className="text-gray-500 text-sm">
                No salary records found yet.
              </p>
            )}
          </motion.div>

          {/* Parking Status */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 lg:col-span-1 cursor-pointer"
            onClick={() => navigate('/parking')}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-sky-100 flex items-center justify-center">
                <Car className="w-6 h-6 text-sky-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-sky-50 text-sky-700">
                {getParkingBadgeText()}
              </span>
            </div>
            {getParkingText()}
          </motion.div>

          {/* Projects & Tasks – UPDATED to navigate */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 lg:col-span-1 cursor-pointer"
            onClick={() => navigate('/projects')}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-purple-100 flex items-center justify-center">
                <Briefcase className="w-6 h-6 text-purple-600" />
              </div>
              <span className="text-xs font-semibold px-3 py-1 rounded-full bg-purple-50 text-purple-700">
                Projects & Tasks
              </span>
            </div>
            <div className="mb-4">
              <p className="text-sm text-gray-500">
                Active projects:{' '}
                <span className="font-semibold text-gray-900">
                  {projects.length}
                </span>
              </p>
            </div>
            <div className="max-h-40 overflow-y-auto space-y-2">
              {tasks.length > 0 ? (
                tasks.slice(0, 4).map((task) => (
                  <div
                    key={task.id}
                    className="flex items-center justify-between bg-gray-50 rounded-2xl px-3 py-2"
                  >
                    <div>
                      <p className="text-sm font-semibold text-gray-800">
                        {task.taskName}
                      </p>
                      <p className="text-xs text-gray-500">
                        Status:{' '}
                        <span className="font-semibold">
                          {task.status}
                        </span>
                      </p>
                    </div>
                    <span className="text-xs font-semibold text-blue-600">
                      {task.completionPercentage ?? 0}%
                    </span>
                  </div>
                ))
              ) : (
                <p className="text-sm text-gray-500">
                  No tasks assigned yet.
                </p>
              )}
            </div>
          </motion.div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <motion.button
            whileHover={{ scale: 1.02, y: -2 }}
            className="btn-success flex items-center justify-between"
            onClick={() => navigate('/attendance')}
          >
            <span>Mark Attendance</span>
            <ArrowRight className="w-5 h-5" />
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.02, y: -2 }}
            className="btn-secondary flex items-center justify-between"
            onClick={() => navigate('/leaves')}
          >
            <span>Apply Leave</span>
            <ArrowRight className="w-5 h-5" />
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.02, y: -2 }}
            className="btn-secondary flex items-center justify-between"
            onClick={() => navigate('/salary')}
          >
            <span>View Salary</span>
            <ArrowRight className="w-5 h-5" />
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.02, y: -2 }}
            className="btn-secondary flex items-center justify-between"
            onClick={() => navigate('/profile')}
          >
            <span>View Profile</span>
            <ArrowRight className="w-5 h-5" />
          </motion.button>
        </div>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">Loading dashboard...</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
