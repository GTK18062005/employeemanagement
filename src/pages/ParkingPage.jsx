import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  Car,
  Loader2,
  AlertCircle,
  CheckCircle2,
  MapPin,
  Info,
  Plus,
} from 'lucide-react';

const inputClass =
  'w-full rounded-xl border border-gray-300 bg-white/90 px-3 py-2 text-gray-900 placeholder-gray-500 shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400';

const ParkingPage = () => {
  const { user } = useAuth();
  const username = user?.username;

  const [allocation, setAllocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [savingVehicle, setSavingVehicle] = useState(false);
  const [requestingParking, setRequestingParking] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [showRequestPopup, setShowRequestPopup] = useState(false); // POPUP

  // vehicle details (from User entity)
  const [vehicleNumber, setVehicleNumber] = useState('');
  const [vehicleType, setVehicleType] = useState('CAR');
  const [vehicleModel, setVehicleModel] = useState('');
  const [vehicleColor, setVehicleColor] = useState('');

  const fetchData = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');
      setMessage('');

      const [allocRes, userRes] = await Promise.all([
        api.get(`/parking/allocations/user/${username}`),
        api.get(`/user/profile/${username}`),
      ]);

      const allocData = allocRes.data;
      setAllocation(allocData.allocation || allocData.parking || null);

      const uData = userRes.data.success ? userRes.data.user : userRes.data;
      setVehicleNumber(uData.vehicleNumber || '');
      setVehicleType(uData.vehicleType || 'CAR');
      setVehicleModel(uData.vehicleModel || '');
      setVehicleColor(uData.vehicleColor || '');
    } catch (e) {
      console.error('PARKING ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message ||
          'Failed to load parking details. Please refresh.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const handleVehicleSave = async (e) => {
    e.preventDefault();
    if (!username) return;

    if (!vehicleNumber.trim()) {
      setError('Vehicle number is required.');
      return;
    }

    try {
      setSavingVehicle(true);
      setError('');
      setMessage('');

      const payload = {
        vehicleNumber: vehicleNumber.trim(),
        vehicleType,
        vehicleModel: vehicleModel.trim(),
        vehicleColor: vehicleColor.trim(),
      };

      const res = await api.put(`/user/vehicle/${username}`, payload);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Failed to update vehicle details.');
        return;
      }

      setMessage(data.message || 'Vehicle details updated successfully.');
      await fetchData();
    } catch (e) {
      console.error(
        'VEHICLE UPDATE ERROR',
        e.response?.status,
        e.response?.data || e
      );
      setError(
        e.response?.data?.message ||
          'Error updating vehicle details. Please try again.'
      );
    } finally {
      setSavingVehicle(false);
    }
  };

  const handleRequestParking = async (e) => {
    e.preventDefault();
    if (!username || !vehicleNumber.trim()) {
      setError('Please add vehicle details first.');
      return;
    }

    try {
      setRequestingParking(true);
      setError('');
      setMessage('');

      const payload = {
        username,
        vehicleNumber: vehicleNumber.trim(),
        vehicleType,
        vehicleModel: vehicleModel.trim(),
        vehicleColor: vehicleColor.trim(),
      };

      // correct backend URL
      const res = await api.post('/parking/requests', payload);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Failed to request parking allocation.');
        return;
      }

      setMessage(
        data.message || 'Parking allocation request submitted successfully!'
      );

      // show popup for 2 seconds
      setShowRequestPopup(true);
      setTimeout(() => setShowRequestPopup(false), 2000);

      await fetchData();
    } catch (e) {
      console.error(
        'PARKING REQUEST ERROR',
        e.response?.status,
        e.response?.data || e
      );
      setError(
        e.response?.data?.message ||
          'Error requesting parking. Please try again.'
      );
    } finally {
      setRequestingParking(false);
    }
  };

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-5xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-sky-100 flex items-center justify-center">
              <Car className="w-6 h-6 text-sky-600" />
            </div>
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
                Parking
              </h1>
              <p className="text-gray-600 mt-1">
                View your allocated parking slot and manage your vehicle details.
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

        {/* Allocation card */}
        <motion.div
          whileHover={{ y: -3, scale: 1.01 }}
          className="card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-sky-100 flex items-center justify-center">
                <MapPin className="w-5 h-5 text-sky-600" />
              </div>
              <div>
                <p className="text-xs font-semibold text-sky-600 uppercase tracking-wide">
                  My parking slot
                </p>
                <p className="text-lg font-semibold text-gray-900">
                  {allocation ? allocation.slotNumber : 'No slot allocated'}
                </p>
              </div>
            </div>
            {allocation && (
              <span
                className={
                  'text-xs font-semibold px-3 py-1 rounded-full ' +
                  (allocation.status === 'ACTIVE'
                    ? 'bg-emerald-50 text-emerald-700'
                    : 'bg-gray-100 text-gray-700')
                }
              >
                {allocation.status || 'ACTIVE'}
              </span>
            )}
          </div>

          {allocation ? (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Location</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">
                  {allocation.level || 'Main Parking'}{' '}
                  {allocation.section ? `• ${allocation.section}` : ''}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Slot type: {allocation.slotType || 'GENERAL'}
                </p>
              </div>

              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Vehicle</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">
                  {allocation.vehicleNumber || vehicleNumber || 'Not linked'}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Type: {allocation.vehicleType || vehicleType || '—'}
                </p>
              </div>

              <div className="bg-gray-50 rounded-2xl px-4 py-3">
                <p className="text-xs text-gray-500">Validity & Notes</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">
                  Valid until: {allocation.validUntil || 'No expiry'}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Remarks: {allocation.remarks || '—'}
                </p>
              </div>
            </div>
          ) : (
            <motion.form
              onSubmit={handleRequestParking}
              className="space-y-4 p-6 bg-orange-50 border-2 border-dashed border-orange-200 rounded-2xl"
            >
              <div className="flex items-start gap-3">
                <Info className="w-5 h-5 mt-0.5 text-orange-500 flex-shrink-0" />
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    No parking allocated
                  </h3>
                  <p className="text-sm text-gray-600">
                    Request a parking slot by submitting your vehicle details
                    below. Your request will be reviewed by the facilities team.
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex flex-col">
                  <label className="mb-1 text-sm font-semibold text-gray-900">
                    Vehicle number <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    className={inputClass}
                    value={vehicleNumber}
                    onChange={(e) =>
                      setVehicleNumber(e.target.value.toUpperCase())
                    }
                    placeholder="TN 01 AB 1234"
                    required
                  />
                </div>

                <div className="flex flex-col">
                  <label className="mb-1 text-sm font-semibold text-gray-900">
                    Vehicle type
                  </label>
                  <select
                    className={inputClass}
                    value={vehicleType}
                    onChange={(e) => setVehicleType(e.target.value)}
                  >
                    <option value="CAR">Car</option>
                    <option value="BIKE">Bike</option>
                    <option value="SUV">SUV</option>
                    <option value="ELECTRIC">Electric</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  type="submit"
                  disabled={requestingParking || !vehicleNumber.trim()}
                  className="btn-primary flex items-center gap-2 bg-orange-500 hover:bg-orange-600 text-white"
                >
                  {requestingParking ? (
                    <Loader2 className="w-4 h-4 animate-spin" />
                  ) : (
                    <Plus className="w-4 h-4" />
                  )}
                  <span>Request Parking Allocation</span>
                </motion.button>
              </div>
            </motion.form>
          )}
        </motion.div>

        {/* Vehicle details form */}
        <motion.form
          whileHover={{ y: -3, scale: 1.01 }}
          className="card p-6 space-y-4"
          onSubmit={handleVehicleSave}
        >
          <div className="flex items-center justify-between mb-1">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-sky-100 flex items-center justify-center">
                <Car className="w-5 h-5 text-sky-600" />
              </div>
              <div>
                <h2 className="text-lg font-semibold text-gray-900">
                  Vehicle details
                </h2>
                <p className="text-xs text-gray-500">
                  These details are linked to your parking allocation and can be
                  used for security and access control.
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Vehicle number
              </label>
              <input
                type="text"
                className={inputClass}
                value={vehicleNumber}
                onChange={(e) =>
                  setVehicleNumber(e.target.value.toUpperCase())
                }
                placeholder="TN 01 AB 1234"
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Vehicle type
              </label>
              <select
                className={inputClass}
                value={vehicleType}
                onChange={(e) => setVehicleType(e.target.value)}
              >
                <option value="CAR">Car</option>
                <option value="BIKE">Bike</option>
                <option value="SUV">SUV</option>
                <option value="ELECTRIC">Electric</option>
              </select>
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Vehicle model
              </label>
              <input
                type="text"
                className={inputClass}
                value={vehicleModel}
                onChange={(e) => setVehicleModel(e.target.value)}
                placeholder="e.g. Hyundai i20, Honda Activa"
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-sm font-semibold text-gray-900">
                Vehicle color
              </label>
              <input
                type="text"
                className={inputClass}
                value={vehicleColor}
                onChange={(e) => setVehicleColor(e.target.value)}
                placeholder="e.g. White, Red, Black"
              />
            </div>
          </div>

          <div className="flex justify-end mt-2">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={savingVehicle}
              className="btn-primary flex items-center gap-2"
            >
              {savingVehicle ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <Car className="w-4 h-4" />
              )}
              <span>Save vehicle details</span>
            </motion.button>
          </div>
        </motion.form>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">Loading parking...</span>
            </div>
          </div>
        )}

        {/* Success popup after request */}
        {showRequestPopup && (
          <div className="fixed inset-0 flex items-center justify-center bg-black/30 z-50">
            <div className="bg-white rounded-2xl shadow-xl px-6 py-4 flex items-center gap-3">
              <CheckCircle2 className="w-5 h-5 text-emerald-500" />
              <div>
                <p className="text-sm font-semibold text-gray-900">
                  Parking request submitted
                </p>
                <p className="text-xs text-gray-500">
                  Your request has been sent to the facilities team.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ParkingPage;
