import { useCallback, useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import ParkingSlotStatusBadge from '../../components/parking/ParkingSlotStatusBadge';
import ParkingAllocationStatusBadge from '../../components/parking/ParkingAllocationStatusBadge';
import Alert from '../../components/ui/Alert';
import Button from '../../components/ui/Button';
import DataTable from '../../components/ui/DataTable';
import Input from '../../components/ui/Input';
import Modal from '../../components/ui/Modal';
import PageHeader from '../../components/ui/PageHeader';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import {
  getAdminParkingSlots,
  createAdminParkingSlot,
  getAdminParkingAllocations,
  createAdminParkingAllocation,
  releaseAdminParkingAllocation,
} from '../../services/parkingService';
import { getAllEmployees } from '../../services/employeeService';
import { useToast } from '../../context/ToastContext';
import { getErrorMessage } from '../../utils/apiError';
import { formatDateTime, getEmployeeFullName } from '../../utils/formatters';

function AdminParkingPage() {
  const toast = useToast();
  const [activeTab, setActiveTab] = useState('slots');
  const [slots, setSlots] = useState([]);
  const [allocations, setAllocations] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Create slot
  const [showSlotForm, setShowSlotForm] = useState(false);
  const [slotForm, setSlotForm] = useState({ slotNumber: '', location: '' });
  const [slotSubmitting, setSlotSubmitting] = useState(false);
  const [slotFormError, setSlotFormError] = useState('');

  // Create allocation
  const [showAllocForm, setShowAllocForm] = useState(false);
  const [allocForm, setAllocForm] = useState({ employeeId: '', slotId: '' });
  const [allocSubmitting, setAllocSubmitting] = useState(false);
  const [allocFormError, setAllocFormError] = useState('');

  // Release
  const [releaseAction, setReleaseAction] = useState(null);
  const [releasing, setReleasing] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const [slotsData, allocData, empData] = await Promise.all([
        getAdminParkingSlots(),
        getAdminParkingAllocations(),
        getAllEmployees().catch(() => []),
      ]);
      setSlots(slotsData);
      setAllocations(allocData);
      setEmployees(empData);
    } catch (loadError) {
      setError(getErrorMessage(loadError, 'Failed to load parking data.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  async function handleCreateSlot(event) {
    event.preventDefault();
    setSlotFormError('');
    setSlotSubmitting(true);
    try {
      await createAdminParkingSlot(slotForm);
      toast.success('Parking slot created.');
      setShowSlotForm(false);
      setSlotForm({ slotNumber: '', location: '' });
      await loadData();
    } catch (err) {
      setSlotFormError(getErrorMessage(err, 'Failed to create slot.'));
    } finally {
      setSlotSubmitting(false);
    }
  }

  async function handleCreateAllocation(event) {
    event.preventDefault();
    setAllocFormError('');
    setAllocSubmitting(true);
    try {
      await createAdminParkingAllocation({
        employeeId: Number(allocForm.employeeId),
        slotId: Number(allocForm.slotId),
      });
      toast.success('Parking allocation created.');
      setShowAllocForm(false);
      setAllocForm({ employeeId: '', slotId: '' });
      await loadData();
    } catch (err) {
      setAllocFormError(getErrorMessage(err, 'Failed to create allocation.'));
    } finally {
      setAllocSubmitting(false);
    }
  }

  async function handleRelease() {
    if (!releaseAction) return;
    setReleasing(true);
    try {
      await releaseAdminParkingAllocation(releaseAction.id);
      toast.success('Parking allocation released.');
      setReleaseAction(null);
      await loadData();
    } catch (err) {
      toast.error(getErrorMessage(err, 'Failed to release allocation.'));
      setReleaseAction(null);
    } finally {
      setReleasing(false);
    }
  }

  const slotColumns = [
    { key: 'slotNumber', header: 'Slot #' },
    { key: 'location', header: 'Location', render: (s) => s.location || '—' },
    { key: 'status', header: 'Status', render: (s) => <ParkingSlotStatusBadge status={s.status} /> },
    { key: 'createdAt', header: 'Created', render: (s) => formatDateTime(s.createdAt) },
  ];

  const allocationColumns = [
    { key: 'employeeName', header: 'Employee', render: (a) => a.employeeName ?? `Employee #${a.employeeId}` },
    { key: 'slotNumber', header: 'Slot', render: (a) => a.slotNumber ?? `Slot #${a.slotId}` },
    { key: 'status', header: 'Status', render: (a) => <ParkingAllocationStatusBadge status={a.status} /> },
    { key: 'createdAt', header: 'Allocated', render: (a) => formatDateTime(a.createdAt) },
    {
      key: 'actions',
      header: 'Actions',
      render: (a) =>
        a.status === 'APPROVED' || a.status === 'REQUESTED' ? (
          <Button variant="danger" size="sm" onClick={() => setReleaseAction({ id: a.id })}>
            Release
          </Button>
        ) : '—',
    },
  ];

  const availableSlots = slots.filter((s) => s.status === 'AVAILABLE');

  return (
    <div className="page">
      <PageHeader title="Parking Management" description="Manage parking slots and allocations.">
        <Button variant="secondary" icon={Plus} onClick={() => setShowSlotForm(true)}>
          Add Slot
        </Button>
        <Button icon={Plus} onClick={() => setShowAllocForm(true)}>
          Allocate
        </Button>
      </PageHeader>

      {error ? (
        <Alert variant="error" title="Error" onDismiss={() => setError('')}>
          {error}
        </Alert>
      ) : null}

      <div className="tabs">
        <button
          type="button"
          className={`tabs__tab${activeTab === 'slots' ? ' tabs__tab--active' : ''}`}
          onClick={() => setActiveTab('slots')}
        >
          Slots ({slots.length})
        </button>
        <button
          type="button"
          className={`tabs__tab${activeTab === 'allocations' ? ' tabs__tab--active' : ''}`}
          onClick={() => setActiveTab('allocations')}
        >
          Allocations ({allocations.length})
        </button>
      </div>

      {loading ? (
        <LoadingState title="Loading parking data" />
      ) : error && !slots.length && !allocations.length ? (
        <ErrorState description={error} onRetry={loadData} />
      ) : activeTab === 'slots' ? (
        <DataTable columns={slotColumns} data={slots} emptyTitle="No parking slots" emptyDescription="Add a parking slot to get started." />
      ) : (
        <DataTable columns={allocationColumns} data={allocations} emptyTitle="No allocations" emptyDescription="Create an allocation to assign a slot." />
      )}

      {/* Create Slot Modal */}
      <Modal
        isOpen={showSlotForm}
        onClose={() => setShowSlotForm(false)}
        title="Add Parking Slot"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setShowSlotForm(false)} disabled={slotSubmitting}>Cancel</Button>
            <Button type="submit" form="slot-form" disabled={slotSubmitting}>{slotSubmitting ? 'Creating...' : 'Create'}</Button>
          </>
        }
      >
        <form id="slot-form" className="stack-form" onSubmit={handleCreateSlot}>
          <Input label="Slot Number" name="slotNumber" value={slotForm.slotNumber} onChange={(e) => setSlotForm((c) => ({ ...c, slotNumber: e.target.value }))} required />
          <Input label="Location" name="location" value={slotForm.location} onChange={(e) => setSlotForm((c) => ({ ...c, location: e.target.value }))} />
          {slotFormError ? <p className="form-error" role="alert">{slotFormError}</p> : null}
        </form>
      </Modal>

      {/* Create Allocation Modal */}
      <Modal
        isOpen={showAllocForm}
        onClose={() => setShowAllocForm(false)}
        title="Allocate Parking"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setShowAllocForm(false)} disabled={allocSubmitting}>Cancel</Button>
            <Button type="submit" form="alloc-form" disabled={allocSubmitting}>{allocSubmitting ? 'Allocating...' : 'Allocate'}</Button>
          </>
        }
      >
        <form id="alloc-form" className="stack-form" onSubmit={handleCreateAllocation}>
          <div className="ui-input">
            <label htmlFor="alloc-employee">Employee</label>
            <select id="alloc-employee" name="employeeId" className="ui-input__field" value={allocForm.employeeId} onChange={(e) => setAllocForm((c) => ({ ...c, employeeId: e.target.value }))} required>
              <option value="">Select employee</option>
              {employees.map((emp) => (
                <option key={emp.id} value={emp.id}>{getEmployeeFullName(emp)} ({emp.employeeCode})</option>
              ))}
            </select>
          </div>
          <div className="ui-input">
            <label htmlFor="alloc-slot">Parking Slot</label>
            <select id="alloc-slot" name="slotId" className="ui-input__field" value={allocForm.slotId} onChange={(e) => setAllocForm((c) => ({ ...c, slotId: e.target.value }))} required>
              <option value="">Select slot</option>
              {availableSlots.map((slot) => (
                <option key={slot.id} value={slot.id}>{slot.slotNumber} — {slot.location || 'No location'}</option>
              ))}
            </select>
          </div>
          {allocFormError ? <p className="form-error" role="alert">{allocFormError}</p> : null}
        </form>
      </Modal>

      <ConfirmationDialog
        isOpen={Boolean(releaseAction)}
        title="Release Allocation"
        message="Are you sure you want to release this parking allocation?"
        confirmLabel="Release"
        confirmVariant="danger"
        loading={releasing}
        onConfirm={handleRelease}
        onCancel={() => setReleaseAction(null)}
      />
    </div>
  );
}

export default AdminParkingPage;
