import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import api from '../api/http';
import { useAuth } from '../contexts/AuthContext';
import {
  User,
  Mail,
  Phone,
  MapPin,
  Briefcase,
  Loader2,
  AlertCircle,
  CheckCircle2,
  Lock,
} from 'lucide-react';

const inputClass =
  'w-full rounded-xl border border-gray-300 bg-white/90 px-3 py-2 text-gray-900 ' +
  'placeholder-gray-500 shadow-sm focus:outline-none focus:ring-2 ' +
  'focus:ring-blue-400 focus:border-blue-400';

const passwordInputClass =
  'w-full rounded-xl border border-gray-300 bg-white/90 px-3 py-2 text-gray-900 ' +
  'placeholder-gray-500 shadow-sm focus:outline-none focus:ring-2 ' +
  'focus:ring-red-400 focus:border-red-400';

const ProfilePage = () => {
  const { user, setUser } = useAuth();
  const username = user?.username;

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [pwdSaving, setPwdSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const [profile, setProfile] = useState(null);

  // profile fields
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [department, setDepartment] = useState('');
  const [designation, setDesignation] = useState('');
  const [address, setAddress] = useState('');
  const [emergencyContact, setEmergencyContact] = useState('');
  const [bankAccountNumber, setBankAccountNumber] = useState('');
  const [panNumber, setPanNumber] = useState('');

  // password fields
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');

  const fetchProfile = async () => {
    if (!username) return;
    try {
      setLoading(true);
      setError('');
      setMessage('');

      const res = await api.get(`/user/profile/${username}`);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Failed to load profile.');
        return;
      }

      const u = data.user;
      setProfile(u);

      setName(u.name || '');
      setEmail(u.email || '');
      setPhone(u.phone || '');
      setDepartment(u.department || '');
      setDesignation(u.designation || '');
      setAddress(u.address || '');
      setEmergencyContact(u.emergencyContact || '');
      setBankAccountNumber(u.bankAccountNumber || '');
      setPanNumber(u.panNumber || '');
    } catch (e) {
      console.error('PROFILE ERROR', e.response?.status, e.response?.data || e);
      setError(
        e.response?.data?.message || 'Failed to load profile. Please refresh.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [username]);

  const handleSave = async (e) => {
    e.preventDefault();
    if (!username) return;

    try {
      setSaving(true);
      setError('');
      setMessage('');

      const payload = {
        name,
        email,
        phone,
        department,
        designation,
        address,
        emergencyContact,
        bankAccountNumber,
        panNumber,
      };

      const res = await api.put(`/user/profile/${username}`, payload);
      const data = res.data;

      if (!data.success) {
        setError(data.message || 'Failed to update profile.');
        return;
      }

      const updated = data.user;
      setProfile(updated);

      if (setUser) {
        setUser((prev) => ({
          ...prev,
          name: updated.name,
          email: updated.email,
          phone: updated.phone,
          department: updated.department,
          designation: updated.designation,
          address: updated.address,
          emergencyContact: updated.emergencyContact,
          bankAccountNumber: updated.bankAccountNumber,
          panNumber: updated.panNumber,
        }));
      }

      setMessage(data.message || 'Profile updated successfully.');
    } catch (e) {
      console.error(
        'PROFILE UPDATE ERROR',
        e.response?.status,
        e.response?.data || e
      );
      setError(
        e.response?.data?.message ||
          'Error updating profile. Please try again.'
      );
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (!username) return;

    setPasswordError('');
    setPasswordMessage('');

    if (!currentPassword || !newPassword || !confirmPassword) {
      setPasswordError('Please fill all password fields.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('New password and confirm password do not match.');
      return;
    }
    if (newPassword.length < 6) {
      setPasswordError('New password must be at least 6 characters.');
      return;
    }

    try {
      setPwdSaving(true);

      const res = await api.post(`/user/change-password/${username}`, {
        currentPassword,
        newPassword,
      });
      const data = res.data;

      if (!data.success) {
        setPasswordError(data.message || 'Failed to change password.');
        return;
      }

      setPasswordMessage(data.message || 'Password changed successfully.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (e) {
      console.error(
        'CHANGE PASSWORD ERROR',
        e.response?.status,
        e.response?.data || e
      );
      setPasswordError(
        e.response?.data?.message ||
          'Error changing password. Please try again.'
      );
    } finally {
      setPwdSaving(false);
    }
  };

  const avatarLetter = (name || username || 'U')
    .toString()
    .charAt(0)
    .toUpperCase();

  return (
    <div className="min-h-screen p-6 sm:p-8">
      <div className="max-w-5xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-4">
            {profile?.profilePicture ? (
              <img
                src={profile.profilePicture}
                alt="Profile"
                className="w-16 h-16 rounded-full object-cover border-2 border-blue-500 shadow-md"
              />
            ) : (
              <div className="w-16 h-16 rounded-full bg-gradient-to-br from-blue-500 via-purple-500 to-indigo-500 flex items-center justify-center text-white font-semibold text-2xl shadow-md">
                {avatarLetter}
              </div>
            )}
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-gray-900">
                Profile
              </h1>
              <p className="text-gray-600 mt-1">
                View and update your personal and job details.
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

        {/* Summary cards */}
        {profile && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="card p-4 flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-blue-100 flex items-center justify-center">
                <User className="w-5 h-5 text-blue-600" />
              </div>
              <div className="text-sm">
                <p className="text-gray-500">Employee</p>
                <p className="font-semibold text-gray-900">{profile.name}</p>
                <p className="text-xs text-gray-500">
                  Username: {profile.username}
                </p>
              </div>
            </div>

            <div className="card p-4 flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-purple-100 flex items-center justify-center">
                <Briefcase className="w-5 h-5 text-purple-600" />
              </div>
              <div className="text-sm">
                <p className="text-gray-500">Job</p>
                <p className="font-semibold text-gray-900">
                  {profile.designation || 'Not set'}
                </p>
                <p className="text-xs text-gray-500">
                  Department: {profile.department || '—'}
                </p>
              </div>
            </div>

            <div className="card p-4 flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-emerald-100 flex items-center justify-center">
                <Phone className="w-5 h-5 text-emerald-600" />
              </div>
              <div className="text-sm">
                <p className="text-gray-500">Contact</p>
                <p className="font-semibold text-gray-900">
                  {profile.phone || 'Not set'}
                </p>
                <p className="text-xs text-gray-500">
                  Emergency: {profile.emergencyContact || '—'}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Edit profile form */}
        <motion.form
          whileHover={{ y: -2 }}
          className="card p-6 space-y-5"
          onSubmit={handleSave}
        >
          <h2 className="text-lg font-semibold text-gray-900 mb-1">
            Personal information
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Full name</label>
              <div className="flex items-center gap-2">
                <User className="w-4 h-4 text-gray-400" />
                <input
                  type="text"
                  className={inputClass}
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </div>
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Email</label>
              <div className="flex items-center gap-2">
                <Mail className="w-4 h-4 text-gray-400" />
                <input
                  type="email"
                  className={inputClass}
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Phone number</label>
              <div className="flex items-center gap-2">
                <Phone className="w-4 h-4 text-gray-400" />
                <input
                  type="tel"
                  className={inputClass}
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </div>
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Address</label>
              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4 text-gray-400" />
                <input
                  type="text"
                  className={inputClass}
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                />
              </div>
            </div>
          </div>

          <h2 className="text-lg font-semibold text-gray-900 mt-4">
            Job information
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Department</label>
              <input
                type="text"
                className={inputClass}
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Designation</label>
              <input
                type="text"
                className={inputClass}
                value={designation}
                onChange={(e) => setDesignation(e.target.value)}
              />
            </div>
          </div>

          <h2 className="text-lg font-semibold text-gray-900 mt-4">
            Other details
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Emergency contact</label>
              <input
                type="text"
                className={inputClass}
                value={emergencyContact}
                onChange={(e) => setEmergencyContact(e.target.value)}
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">Bank account number</label>
              <input
                type="text"
                className={inputClass}
                value={bankAccountNumber}
                onChange={(e) => setBankAccountNumber(e.target.value)}
              />
            </div>

            <div className="flex flex-col">
              <label className="mb-1 text-gray-600">PAN number</label>
              <input
                type="text"
                className={inputClass}
                value={panNumber}
                onChange={(e) => setPanNumber(e.target.value)}
              />
            </div>
          </div>

          <div className="flex justify-end mt-4">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={saving}
              className="btn-primary flex items-center gap-2"
            >
              {saving ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <CheckCircle2 className="w-4 h-4" />
              )}
              <span>Save changes</span>
            </motion.button>
          </div>
        </motion.form>

        {/* Change Password section */}
        <motion.form
          whileHover={{ y: -2 }}
          className="card p-6 space-y-4"
          onSubmit={handlePasswordChange}
        >
          <div className="flex items-center gap-3 mb-1">
            <div className="w-10 h-10 rounded-2xl bg-red-100 flex items-center justify-center">
              <Lock className="w-5 h-5 text-red-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">
                Change password
              </h2>
              <p className="text-xs text-gray-500">
                Update your account password. Use at least 6 characters.
              </p>
            </div>
          </div>

          {passwordMessage && (
            <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-2xl text-sm">
              <CheckCircle2 className="w-4 h-4" />
              <span>{passwordMessage}</span>
            </div>
          )}
          {passwordError && (
            <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-2xl text-sm">
              <AlertCircle className="w-4 h-4" />
              <span>{passwordError}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div className="flex flex-col">
              <span className="mb-1 text-sm font-semibold text-gray-900">
                Current password
              </span>
              <input
                type="password"
                className={passwordInputClass}
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                placeholder="Enter current password"
              />
            </div>

            <div className="flex flex-col">
              <span className="mb-1 text-sm font-semibold text-gray-900">
                New password
              </span>
              <input
                type="password"
                className={passwordInputClass}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Enter new password"
              />
            </div>

            <div className="flex flex-col">
              <span className="mb-1 text-sm font-semibold text-gray-900">
                Confirm new password
              </span>
              <input
                type="password"
                className={passwordInputClass}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Re-enter new password"
              />
            </div>
          </div>

          <div className="flex justify-end mt-2">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={pwdSaving}
              className="btn-secondary flex items-center gap-2"
            >
              {pwdSaving ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <Lock className="w-4 h-4" />
              )}
              <span>Update password</span>
            </motion.button>
          </div>
        </motion.form>

        {loading && (
          <div className="fixed inset-0 bg-black/5 flex items-center justify-center pointer-events-none">
            <div className="bg-white/80 backdrop-blur-md px-6 py-4 rounded-2xl shadow-lg flex items-center space-x-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span className="text-sm text-gray-700">Loading profile...</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
