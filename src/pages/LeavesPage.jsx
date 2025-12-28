import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  CalendarDays,
  FileText,
  CheckCircle2,
  AlertCircle,
  Loader2,
} from 'lucide-react';

const LeavesPage = () => {
  const { user } = useAuth();
  const username = user?.username;

  const [balance, setBalance] = useState(null);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formLoading, setFormLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  // success popup
  const [popup, setPopup] = useState({
    visible: false,
    title: '',
    description: '',
  });

  // form state
  const [leaveType, setLeaveType] = useState('SICK');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [dayType, setDayType] = useState('FULL_DAY'); // for UI only
  const [reason, setReason] = useState('');
  const [emergencyContact, setEmergencyContact] = useState('');
  const [handoverNotes, setHandoverNotes] = useState('');

  const showPopup = (title, description) => {
    setPopup({ visible: true, title, description });
    setTimeout(() => {
      setPopup((p) => ({ ...p, visible: false }));
    }, 2000);
  };

  const fetchAll = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');

      const [balanceRes, listRes] = await Promise.all([
        api.get(`/leaves/balance/${username}`),
        api.get(`/leaves/user/${username}`),
      ]);

      setBalance(balanceRes.data.balance || null);
      // backend key is "leaves"
      setRequests(listRes.data.leaves || []);
    } catch (e) {
      console.error(e);
      setError(
        e.response?.data?.message ||
          'Failed to load leave data. Please refresh.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username) return;

    if (!startDate || !endDate || !reason.trim()) {
      setError('Please select start date, end date and enter a reason.');
      return;
    }

    if (startDate > endDate) {
      setError('End date cannot be before start date.');
      return;
    }

    try {
      setFormLoading(true);
      setError('');
      setMessage('');

      // client-side number of days (backend also recalculates)
      const s = new Date(startDate);
      const eDate = new Date(endDate);
      const diffMs = eDate.getTime() - s.getTime();
      const days = Math.floor(diffMs / (1000 * 60 * 60 * 24)) + 1;
      const numberOfDays = days > 0 ? days : 1;

      const payload = {
        username,
        leaveType,          // SICK/CASUAL/EARNED/MATERNITY/PATERNITY/OTHER
        startDate,          // "YYYY-MM-DD"
        endDate,
        numberOfDays,
        reason: reason.trim(),
        emergencyContact: emergencyContact || null,
        handoverNotes: handoverNotes || null,
        // status & appliedDate are defaulted in the entity constructor
      };

      const res = await api.post('/leaves/apply', payload);
      const data = res.data;

      if (data.success) {
        const msg =
          data.message || 'Leave request submitted successfully.';
        setMessage(msg);
        showPopup('Leave request submitted', msg);

        // reset form
        setLeaveType('SICK');
        setStartDate('');
        setEndDate('');
        setDayType('FULL_DAY');
        setReason('');
        setEmergencyContact('');
        setHandoverNotes('');

        await fetchAll();
      } else {
        setError(data.message || 'Leave request failed.');
      }
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          'Leave request failed. Please try again.'
      );
    } finally {
      setFormLoading(false);
    }
  };

  const statusColor = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'text-emerald-600 bg-emerald-50';
      case 'REJECTED':
        return 'text-red-600 bg-red-50';
      case 'PENDING':
      default:
        return 'text-amber-600 bg-amber-50';
    }
  };

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-6xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
              Leaves
            </h1>
            <p className="text-gray-600 mt-1">
              View your leave balance and submit new leave requests.
            </p>
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

        {/* Balance + Apply form */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Balance card */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 lg:col-span-1"
          >
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-2xl bg-emerald-100 flex items-center justify-center">
                <CalendarDays className="w-5 h-5 text-emerald-600" />
              </div>
              <div>
                <p className="text-xs font-semibold text-emerald-600 uppercase tracking-wide">
                  Leave Balance
                </p>
                <p className="text-lg font-semibold text-gray-900">
                  {user?.name || user?.username}
                </p>
              </div>
            </div>
            {balance ? (
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div className="bg-emerald-50 rounded-2xl px-3 py-2">
                  <p className="text-xs text-emerald-700">Sick</p>
                  <p className="text-lg font-bold text-emerald-900">
                    {balance.sickLeaves ?? 0}
                  </p>
                </div>
                <div className="bg-blue-50 rounded-2xl px-3 py-2">
                  <p className="text-xs text-blue-700">Casual</p>
                  <p className="text-lg font-bold text-blue-900">
                    {balance.casualLeaves ?? 0}
                  </p>
                </div>
                <div className="bg-amber-50 rounded-2xl px-3 py-2">
                  <p className="text-xs text-amber-700">Earned</p>
                  <p className="text-lg font-bold text-amber-900">
                    {balance.earnedLeaves ?? 0}
                  </p>
                </div>
                <div className="bg-purple-50 rounded-2xl px-3 py-2">
                  <p className="text-xs text-purple-700">Special</p>
                  <p className="text-lg font-bold text-purple-900">
                    {(balance.maternityLeaves ?? 0) +
                      (balance.paternityLeaves ?? 0)}
                  </p>
                </div>
              </div>
            ) : (
              <p className="text-sm text-gray-500">
                No leave balance information available.
              </p>
            )}
          </motion.div>

          {/* Apply Leave form */}
          <motion.div
            whileHover={{ y: -4, scale: 1.01 }}
            className="card p-6 lg:col-span-2"
          >
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-2xl bg-blue-100 flex items-center justify-center">
                <FileText className="w-5 h-5 text-blue-600" />
              </div>
              <div>
                <p className="text-xs font-semibold text-blue-600 uppercase tracking-wide">
                  Apply for leave
                </p>
                <p className="text-lg font-semibold text-gray-900">
                  New leave request
                </p>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 text-sm">
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">Leave type</label>
                  <select
                    className="input"
                    value={leaveType}
                    onChange={(e) => setLeaveType(e.target.value)}
                  >
                    <option value="SICK">Sick Leave</option>
                    <option value="CASUAL">Casual Leave</option>
                    <option value="EARNED">Earned Leave</option>
                    <option value="MATERNITY">Maternity Leave</option>
                    <option value="PATERNITY">Paternity Leave</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>

                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">Start date</label>
                  <input
                    type="date"
                    className="input"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>

                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">End date</label>
                  <input
                    type="date"
                    className="input"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">Day type</label>
                  <select
                    className="input"
                    value={dayType}
                    onChange={(e) => setDayType(e.target.value)}
                  >
                    <option value="FULL_DAY">Full day</option>
                    <option value="FIRST_HALF">First half</option>
                    <option value="SECOND_HALF">Second half</option>
                  </select>
                </div>

                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">
                    Emergency contact (optional)
                  </label>
                  <input
                    type="text"
                    className="input"
                    value={emergencyContact}
                    onChange={(e) => setEmergencyContact(e.target.value)}
                    placeholder="+91-XXXXXXXXXX"
                  />
                </div>

                <div className="flex flex-col">
                  <label className="text-gray-600 mb-1">
                    Handover notes (optional)
                  </label>
                  <input
                    type="text"
                    className="input"
                    value={handoverNotes}
                    onChange={(e) => setHandoverNotes(e.target.value)}
                    placeholder="Who will handle your work, etc."
                  />
                </div>
              </div>

              <div className="flex flex-col">
                <label className="text-gray-600 mb-1">Reason</label>
                <textarea
                  rows={3}
                  className="input resize-none"
                  placeholder="Example: Personal work, medical appointment, family function, etc."
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                />
              </div>

              <div className="flex justify-end">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  type="submit"
                  disabled={formLoading}
                  className="btn-primary flex items-center gap-2"
                >
                  {formLoading ? (
                    <Loader2 className="w-4 h-4 animate-spin" />
                  ) : (
                    <CheckCircle2 className="w-4 h-4" />
                  )}
                  <span>Submit Leave Request</span>
                </motion.button>
              </div>
            </form>
          </motion.div>
        </div>

        {/* Leave History table */}
        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">
              Leave history
            </h2>
            <p className="text-xs text-gray-500">
              Showing last {requests.length} requests
            </p>
          </div>

          {requests.length === 0 ? (
            <p className="text-sm text-gray-500">
              No leave requests submitted yet.
            </p>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b border-gray-100">
                    <th className="py-2 pr-4">Start</th>
                    <th className="py-2 pr-4">End</th>
                    <th className="py-2 pr-4">Type</th>
                    <th className="py-2 pr-4">Days</th>
                    <th className="py-2 pr-4">Status</th>
                    <th className="py-2 pr-4">Reason</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((req) => (
                    <tr
                      key={req.id}
                      className="border-b border-gray-50 hover:bg-gray-50/80 transition-colors"
                    >
                      <td className="py-2 pr-4 text-gray-800">
                        {req.startDate}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {req.endDate}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {req.leaveType}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {req.numberOfDays}
                      </td>
                      <td className="py-2 pr-4">
                        <span
                          className={
                            'px-2.5 py-1 rounded-full text-xs font-semibold ' +
                            statusColor(req.status)
                          }
                        >
                          {req.status}
                        </span>
                      </td>
                      <td className="py-2 pr-4 text-gray-500 max-w-xs truncate">
                        {req.reason}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">
                Loading leaves...
              </span>
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

export default LeavesPage;
