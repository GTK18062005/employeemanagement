import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import { Wallet, CalendarDays, Loader2, AlertCircle } from 'lucide-react';

const SalaryPage = () => {
  const { user } = useAuth();
  const username = user?.username;

  const [latest, setLatest] = useState(null);
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAll = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');

      // change URLs if your backend uses different ones
      const [latestRes, listRes] = await Promise.all([
        api.get(`/salaries/user/${username}/latest`),
        api.get(`/salaries/user/${username}`), // expected to return ALL salaries from joining
      ]);

      setLatest(latestRes.data.salary || null);
      setList(listRes.data.salaries || []);
    } catch (e) {
      console.error(e);
      setError('Failed to load salary information. Please refresh.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const formatMoney = (amount) =>
    !amount && amount !== 0
      ? '₹0.00'
      : `₹${Number(amount).toLocaleString('en-IN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })}`;

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-6xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
              Salary
            </h1>
            <p className="text-gray-600 mt-1">
              Complete salary details from your date of joining.
            </p>
          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-2xl text-sm">
            <AlertCircle className="w-4 h-4" />
            <span>{error}</span>
          </div>
        )}

        {/* Latest salary card */}
        <motion.div
          whileHover={{ y: -4, scale: 1.01 }}
          className="card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-2xl bg-indigo-100 flex items-center justify-center">
                <Wallet className="w-6 h-6 text-indigo-600" />
              </div>
              <div>
                <p className="text-xs font-semibold text-indigo-600 uppercase tracking-wide">
                  Latest Salary
                </p>
                <p className="text-lg font-semibold text-gray-900">
                  {user?.name || user?.username}
                </p>
              </div>
            </div>
            {latest && (
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <CalendarDays className="w-4 h-4" />
                <span>
                  {latest.salaryMonth} {latest.salaryYear}
                </span>
              </div>
            )}
          </div>

          {latest ? (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Department</p>
                <p className="text-lg font-semibold text-gray-900 mt-1">
                  {latest.department}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Employee ID: {latest.employeeCode}
                </p>
              </div>

              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Gross Salary</p>
                <p className="text-lg font-semibold text-gray-900 mt-1">
                  {formatMoney(latest.grossSalary)}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Net Salary:{' '}
                  <span className="font-semibold">
                    {formatMoney(latest.netSalary)}
                  </span>
                </p>
              </div>

              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Deductions & Status</p>
                <p className="text-sm text-gray-700 mt-1">
                  PF: {formatMoney(latest.pfAmount)} · ESI:{' '}
                  {formatMoney(latest.esiAmount)}
                </p>
                <p className="text-sm text-gray-700 mt-1">
                  Tax: {formatMoney(latest.taxAmount)}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Status:{' '}
                  <span className="font-semibold text-emerald-600">
                    {latest.paymentStatus}
                  </span>
                </p>
              </div>
            </div>
          ) : (
            <p className="text-sm text-gray-500">
              No salary records available yet.
            </p>
          )}
        </motion.div>

        {/* Salary history table (all months from joining) */}
        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">
              Salary history
            </h2>
            <p className="text-xs text-gray-500">
              Showing {list.length} records from joining date
            </p>
          </div>

          {list.length === 0 ? (
            <p className="text-sm text-gray-500">
              No salary data to display.
            </p>
          ) : (
            <div className="overflow-x-auto max-h-[420px]">
              <table className="min-w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b border-gray-100">
                    <th className="py-2 pr-4">Month</th>
                    <th className="py-2 pr-4">Department</th>
                    <th className="py-2 pr-4">Gross</th>
                    <th className="py-2 pr-4">Net</th>
                    <th className="py-2 pr-4">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {list.map((s) => (
                    <tr
                      key={s.id}
                      className="border-b border-gray-50 hover:bg-gray-50/80 transition-colors"
                    >
                      <td className="py-2 pr-4 text-gray-800">
                        {s.salaryMonth} {s.salaryYear}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {s.department}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {formatMoney(s.grossSalary)}
                      </td>
                      <td className="py-2 pr-4 text-gray-800">
                        {formatMoney(s.netSalary)}
                      </td>
                      <td className="py-2 pr-4">
                        <span
                          className={
                            'px-2.5 py-1 rounded-full text-xs font-semibold ' +
                            (s.paymentStatus === 'PAID'
                              ? 'text-emerald-600 bg-emerald-50'
                              : 'text-amber-600 bg-amber-50')
                          }
                        >
                          {s.paymentStatus}
                        </span>
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
              <span className="text-sm text-gray-700">Loading salary...</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SalaryPage;
