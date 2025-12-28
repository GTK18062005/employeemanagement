import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  Clock,
  CheckCircle2,
  AlertCircle,
  Edit3,
  CalendarDays,
} from 'lucide-react';

const inputClass =
  'w-full rounded-xl border border-gray-300 bg-white/90 px-3 py-2 text-gray-900 placeholder-gray-500 shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400';

const AttendancePage = () => {
  const { user } = useAuth();
  const username = user?.username;

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [todayAttendance, setTodayAttendance] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  // popup
  const [popup, setPopup] = useState({
    visible: false,
    title: '',
    description: '',
  });

  // manual form state
  const [manualStatus, setManualStatus] = useState('PRESENT');
  const [manualCheckIn, setManualCheckIn] = useState('09:30');
  const [manualCheckOut, setManualCheckOut] = useState('18:30');
  const [manualNotes, setManualNotes] = useState('');

  const fetchTodayAttendance = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');
      setMessage('');

      const res = await api.get(`/attendance/today/${username}`);
      const data = res.data;

      setTodayAttendance(data.attendance || null);
    } catch (e) {
      console.error('ATTENDANCE LOAD ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Failed to load today attendance. Please refresh.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTodayAttendance();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const showPopup = (title, description) => {
    setPopup({ visible: true, title, description });
    setTimeout(() => setPopup((p) => ({ ...p, visible: false })), 2000);
  };

  const handleCheckIn = async () => {
    if (!username) return;
    try {
      setSubmitting(true);
      setError('');
      setMessage('');

      const res = await api.post(`/attendance/checkin/${username}`);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Check-in failed.');
        return;
      }

      setMessage(data.message || 'Check-in recorded successfully.');
      showPopup('Check-in successful', data.message || 'Your check-in has been recorded.');
      await fetchTodayAttendance();
    } catch (e) {
      console.error('CHECKIN ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Check-in failed. Please try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleCheckOut = async () => {
    if (!username) return;
    try {
      setSubmitting(true);
      setError('');
      setMessage('');

      const res = await api.post(`/attendance/checkout/${username}`);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Check-out failed.');
        return;
      }

      setMessage(data.message || 'Check-out recorded successfully.');
      showPopup('Check-out successful', data.message || 'Your check-out has been recorded.');
      await fetchTodayAttendance();
    } catch (e) {
      console.error('CHECKOUT ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Check-out failed. Please try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  // helper: convert "HH:mm" to "HH:mm:00" (LocalTime string)
  const toTimeString = (hhmm) =>
    hhmm && hhmm.length === 5 ? `${hhmm}:00` : null;

  const handleManualSubmit = async (e) => {
    e.preventDefault();
    if (!username) return;

    if (!manualStatus) {
      setError('Please select a status.');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      setMessage('');

      const payload = {
        username,
        status: manualStatus,
        notes: manualNotes || null,
      };

      // Only send times if user filled them
      const inStr = toTimeString(manualCheckIn);
      const outStr = toTimeString(manualCheckOut);
      if (inStr) payload.checkInTime = inStr;
      if (outStr) payload.checkOutTime = outStr;

      const res = await api.post('/attendance/manual', payload);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Manual attendance request failed. Please try again.');
        return;
      }

      setMessage(data.message || 'Manual attendance recorded successfully.');
      showPopup(
        'Manual attendance saved',
        data.message || 'Your manual attendance has been recorded.'
      );
      await fetchTodayAttendance();
    } catch (e) {
      console.error('MANUAL ATTENDANCE ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Manual attendance request failed. Please try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const todayDateText = todayAttendance?.attendanceDate || new Date().toISOString().slice(0, 10);

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-5xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-blue-100 flex items-center justify-center">
              <Clock className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
                Attendance
              </h1>
              <p className="text-gray-600 mt-1">
                Mark today&apos;s attendance or record it manually.
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

        {/* Today summary + buttons */}
        <motion.div
          whileHover={{ y: -3, scale: 1.01 }}
          className="card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <CalendarDays className="w-5 h-5 text-blue-600" />
              <div>
                <p className="text-xs font-semibold text-blue-600 uppercase tracking-wide">
                  Today&apos;s attendance
                </p>
                <p className="text-sm text-gray-500">{todayDateText}</p>
              </div>
            </div>
            <span className="text-xs font-semibold px-3 py-1 rounded-full bg-blue-50 text-blue-700">
              {todayAttendance?.status || 'Not Marked'}
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div className="bg-gray-50 rounded-2xl px-4 py-3">
              <p className="text-xs text-gray-500">Check-in time</p>
              <p className="text-lg font-semibold text-gray-900 mt-1">
                {todayAttendance?.checkInTime
                  ? todayAttendance.checkInTime.toString().slice(0, 5)
                  : '--'}
              </p>
            </div>
            <div className="bg-gray-50 rounded-2xl px-4 py-3">
              <p className="text-xs text-gray-500">Check-out time</p>
              <p className="text-lg font-semibold text-gray-900 mt-1">
                {todayAttendance?.checkOutTime
                  ? todayAttendance.checkOutTime.toString().slice(0, 5)
                  : '--'}
              </p>
            </div>
            <div className="bg-gray-50 rounded-2xl px-4 py-3">
              <p className="text-xs text-gray-500">Working hours</p>
              <p className="text-lg font-semibold text-gray-900 mt-1">
                {todayAttendance?.workingHours ?? '--'} hrs
              </p>
            </div>
          </div>

          <div className="flex flex-wrap gap-3 mt-4">
            <motion.button
              whileHover={{ scale: 1.02, y: -1 }}
              whileTap={{ scale: 0.98 }}
              disabled={submitting}
              onClick={handleCheckIn}
              className="btn-success px-4 py-2 rounded-xl text-sm font-semibold flex items-center gap-2"
            >
              <Clock className="w-4 h-4" />
              <span>Check in now</span>
            </motion.button>

            <motion.button
              whileHover={{ scale: 1.02, y: -1 }}
              whileTap={{ scale: 0.98 }}
              disabled={submitting}
              onClick={handleCheckOut}
              className="btn-secondary px-4 py-2 rounded-xl text-sm font-semibold flex items-center gap-2"
            >
              <Clock className="w-4 h-4" />
              <span>Check out now</span>
            </motion.button>
          </div>
        </motion.div>

        {/* Manual attendance form */}
        <motion.form
          whileHover={{ y: -3, scale: 1.01 }}
          className="card p-6 space-y-4"
          onSubmit={handleManualSubmit}
        >
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-2xl bg-amber-100 flex items-center justify-center">
              <Edit3 className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">
                Manual attendance
              </h2>
              <p className="text-xs text-gray-500">
                Use this when you need to correct or manually record check-in / check-out times.
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Status
              </label>
              <select
                className={inputClass}
                value={manualStatus}
                onChange={(e) => setManualStatus(e.target.value)}
              >
                <option value="PRESENT">Present</option>
                <option value="ABSENT">Absent</option>
                <option value="HALF_DAY">Half Day</option>
                <option value="LEAVE">Leave</option>
              </select>
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Check-in time (optional)
              </label>
              <input
                type="time"
                className={inputClass}
                value={manualCheckIn}
                onChange={(e) => setManualCheckIn(e.target.value)}
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Check-out time (optional)
              </label>
              <input
                type="time"
                className={inputClass}
                value={manualCheckOut}
                onChange={(e) => setManualCheckOut(e.target.value)}
              />
            </div>
          </div>

          <div className="flex flex-col">
            <label className="mb-1 text-sm font-semibold text-gray-900">
              Notes (optional)
            </label>
            <textarea
              className={inputClass}
              rows={3}
              value={manualNotes}
              onChange={(e) => setManualNotes(e.target.value)}
              placeholder="Reason for manual update, corrections, etc."
            />
          </div>

          <div className="flex justify-end">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={submitting}
              className="btn-primary flex items-center gap-2"
            >
              {submitting ? (
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <Edit3 className="w-4 h-4" />
              )}
              <span>Save manual attendance</span>
            </motion.button>
          </div>
        </motion.form>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">Loading attendance...</span>
            </div>
          </div>
        )}

        {/* Success popup */}
        {popup.visible && (
          <div className="fixed inset-0 flex items-center justify-center bg-black/30 z-50">
            <div className="bg-white rounded-2xl shadow-xl px-6 py-4 flex items-center gap-3">
              <CheckCircle2 className="w-5 h-5 text-emerald-500" />
              <div>
                <p className="text-sm font-semibold text-gray-900">
                  {popup.title}
                </p>
                <p className="text-xs text-gray-500">
                  {popup.description}
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AttendancePage;
